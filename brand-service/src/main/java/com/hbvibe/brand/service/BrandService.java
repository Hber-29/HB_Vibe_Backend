package com.hbvibe.brand.service;

import com.hbvibe.brand.dto.request.AddMemberRequest;
import com.hbvibe.brand.dto.request.BrandCreateRequest;
import com.hbvibe.brand.dto.request.UpdateBrandRequest;
import com.hbvibe.brand.dto.request.UpdateRoleRequest;
import com.hbvibe.brand.dto.response.BrandCreateResponse;
import com.hbvibe.brand.dto.response.UpdateBrandResponse;
import com.hbvibe.brand.entity.Brand;
import com.hbvibe.brand.entity.BrandMember;
import com.hbvibe.brand.entity.BrandRole;
import com.hbvibe.brand.entity.BrandStatus;
import com.hbvibe.brand.exception.AppException;
import com.hbvibe.brand.exception.ErrorCode;
import com.hbvibe.brand.mapper.BrandMapper;
import com.hbvibe.brand.repository.BrandMemberRepository;
import com.hbvibe.brand.repository.BrandRepository;
import com.hbvibe.brand.service.keycloak.KeycloakGroupService;
import com.hbvibe.event.dto.Channel;
import com.hbvibe.event.dto.NotificationEvent;
import com.hbvibe.event.dto.Recepient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class BrandService {
    BrandRepository brandRepository;
    BrandMemberRepository brandMemberRepository;
    BrandMapper  brandMapper;
    StringRedisTemplate stringRedisTemplate;
    KeycloakGroupService keycloakGroupService;
    KafkaTemplate<String, Object> kafkaTemplate;




    @Transactional
    public BrandCreateResponse createBrand(BrandCreateRequest brandCreateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String userEmail = "";
        String ownerName = "";
        if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
            userEmail = (String) jwtAuthToken.getTokenAttributes().get("email");
            if (jwtAuthToken.getTokenAttributes().containsKey("preferred_username")) {
                ownerName = (String) jwtAuthToken.getTokenAttributes().get("preferred_username");
            }
        }
        log.info("userEmail: {}", userEmail);
        log.info("ownerName: {}", ownerName);
        if (userEmail == null || userEmail.isEmpty()) {
            log.info("Không tìm thấy email trong Token của user: {}", userId);
            // throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }

        if (brandRepository.existsByName(brandCreateRequest.getName())) {
            throw new AppException(ErrorCode.BRAND_NAME_EXITED);
        }
        String generatedSlug = generateSlug(brandCreateRequest.getName());
        // nếu name nghĩa khác nhưng khi bỏ dấu lại giống nha vè slug thfi sẽ thêm 1 mã uuid để phân biệt
        if(brandRepository.existsBySlug(generatedSlug)) {
            generatedSlug = generatedSlug + "-" + UUID.randomUUID().toString().substring(0, 5);
        }
        // đoạn logic để xử lý user tối đa chỉ tạo được 3 brand
        // tạo key
        String countKey= "brand_count:" + userId;
        // lấy value từ key
        String currentCountStr = stringRedisTemplate.opsForValue().get(countKey);
        long currentCount = 0;
        if (currentCountStr != null) {
            // nếu value ! null thì lấy gia strij và chuyển nó thành long
            currentCount = Long.parseLong(currentCountStr);
        }else{
            //= null thì tạo mới
            currentCount = brandMemberRepository.countByUserIdAndRole(userId,BrandRole.OWNER);
            stringRedisTemplate.opsForValue().set(countKey, String.valueOf(currentCount));

        }
        if (currentCount >= 3) {
            throw new AppException(ErrorCode.MAX_BRAND_LIMIT_REACHED);
        }
        stringRedisTemplate.opsForValue().increment(countKey);

        Brand brand = Brand.builder()
                .name(brandCreateRequest.getName())
                .slug(generatedSlug)
                .description(brandCreateRequest.getDescription())
                .country(brandCreateRequest.getCountry())
                .logo(brandCreateRequest.getLogo())
                .status(BrandStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Brand createdBrand = brandRepository.save(brand);

        BrandMember brandMember = BrandMember.builder()
                .brand(createdBrand)
                .userId(userId)
                .role(BrandRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build();
        brandMemberRepository.save(brandMember);
        // logic xử lý việc lưu cặp khóa userid:brandid để đối chiếu
        String redisKey = String.format("brand_role:%s:%s", userId, createdBrand.getId());
        stringRedisTemplate.opsForValue().set(redisKey, BrandRole.OWNER.name());
        //add user vào đúng brand
        keycloakGroupService.addUserGruop(userId, "BRAND_OWNER");
        log.info("Tạo Brand [{}] thành công. Đã đồng bộ quyền cho user: {}", createdBrand.getName(), userId);
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .channel(Channel.EMAIL)
                .recipient(List.of(new Recepient(ownerName
                        ,userEmail)))
                .templateCode(2)
                .param(Map.of(
                        "brandName",brand.getName(),
                        "country" ,brand.getCountry(),
                        "createdAt",brand.getCreatedAt().toString()
                ))
                .subject("Welcome to HBvibe")
                .body("Hello" + brand.getName())
                .timestamp(System.currentTimeMillis())
                .build();
        kafkaTemplate.send("notification-brand", notificationEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error(" Gửi Kafka thất bại", ex);
                    } else {
                        log.info(
                                " Gửi Kafka thành công: topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });
        BrandCreateResponse brandCreateResponse = brandMapper.toBrandCreateResponse(brand);
        brandCreateResponse.setBrandId(createdBrand.getId());
        return brandCreateResponse;
    }
    private String generateSlug(String input) {
        if (input == null || input.isEmpty()) return "";
        String noWhiteSpace = input.trim().toLowerCase();
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        slug = slug.replaceAll("đ", "d").replaceAll("Đ", "D");
        slug = slug.replaceAll("[\\s_]+", "-");
        slug = slug.replaceAll("[^a-z0-9\\-]", "");
        return slug.replaceAll("-+", "-");
    }

    // hàm thêm thành viên cho brand
    public void addMemberBrand(AddMemberRequest addMemberRequest) {

        List<UserRepresentation> user = keycloakGroupService.searchUser(addMemberRequest.getStaffEmail());
        log.info("Du lieu nhap vao la :{}",user);
        if(user.isEmpty()){
            throw new AppException(ErrorCode.USERID_NOT_EXISTS);
        }
        String staffUserId = user.get(0).getId();

        Brand brand = brandRepository.findById(addMemberRequest.getBrandId())
                .orElseThrow(()-> new AppException(ErrorCode.BRAND_NAME_EXITED));
        BrandMember brandMember = BrandMember.builder()
                .brand(brand)
                .userId(staffUserId)
                .role(BrandRole.STAFF)
                .joinedAt(LocalDateTime.now())
                .build();
        brandMemberRepository.save(brandMember);
        String redisKey = String.format("brand_role:%s:%s", staffUserId, addMemberRequest.getBrandId());
        stringRedisTemplate.opsForValue().set(redisKey, BrandRole.STAFF.name());
        keycloakGroupService.addUserGruop(staffUserId, "BRAND_STAFF");
        log.info("Thêm Thành viên [{}] thành công. Đã đồng bộ quyền cho user: {}", user.get(0).getUsername(), staffUserId);



    }
    @Transactional
    public UpdateBrandResponse updateBrand(String userId,String brandId,UpdateBrandRequest updateBrandRequest) {
        checkPermission(userId,brandId,List.of(BrandRole.OWNER,BrandRole.MANAGER));
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(()-> new AppException(ErrorCode.VALUE_NULL));
        brand.setName(updateBrandRequest.getName());
        brand.setSlug(generateSlug(updateBrandRequest.getName()));
        brand.setLogo(updateBrandRequest.getLogo());
        brand.setDescription(updateBrandRequest.getDescription());
        brand.setCountry(updateBrandRequest.getCountry());
        brand.setStatus(updateBrandRequest.getStatus());
        brand.setUpdatedAt(LocalDateTime.now());

        brand = brandRepository.save(brand);

        return brandMapper.toUpdateBrandResponse(brand);

    }
    // hàm update role của member
    @Transactional
    public void updateMemberRole(String brandId,String requesterUserId,String targetUserId, UpdateRoleRequest newRole){
        log.info("Du lieu nhap vao la :{}",newRole);
        checkPermission(requesterUserId,brandId,List.of(BrandRole.OWNER));
        BrandMember member = brandMemberRepository.findByBrandIdAndUserId(brandId,targetUserId)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));

        BrandRole oldRole = member.getRole();
        if(oldRole== newRole.getBrandRole()) return;
        if(oldRole== BrandRole.OWNER){
            throw new AppException(ErrorCode.CANNOT_CHANGE_OWNER_ROLE);
        }
        member.setRole(newRole.getBrandRole());
        brandMemberRepository.save(member);
        //cập nhật lại redis
        String redisKey= String.format("brand_role:%s:%s",targetUserId,brandId);
        stringRedisTemplate.opsForValue().set(redisKey,newRole.getBrandRole().name());
        // cập nhật lại keycloak
        String oldGruopName= "BRAND_" + oldRole;
        String newGruopName= "BRAND_" + newRole.getBrandRole();
        keycloakGroupService.removeUserFromGroup(targetUserId,oldGruopName);
        keycloakGroupService.addUserGruop(targetUserId,newGruopName);
        log.info("Owner {} đã đổi quyền của {} từ {} sang {}", requesterUserId, targetUserId, oldRole, newRole);
    }

    private void checkPermission(String userId, String brandId, List<BrandRole> allowedRoles) {
        String redisKey= String.format("brand_role:%s:%s", userId, brandId);
        log.info("Đang kiểm tra quyền với Redis Key: [{}]", redisKey);
        String currentRole = stringRedisTemplate.opsForValue().get(redisKey);
        log.info("Quyền lấy ra từ Redis là: [{}]", currentRole);
        if (currentRole == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        boolean hasPermission = allowedRoles.stream().anyMatch(role -> role.name().equals(currentRole));
        if(!hasPermission){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }


}
