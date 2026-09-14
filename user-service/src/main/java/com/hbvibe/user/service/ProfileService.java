package com.hbvibe.user.service;

import com.hbvibe.event.dto.Channel;
import com.hbvibe.event.dto.NotificationEvent;
import com.hbvibe.event.dto.Recepient;
import com.hbvibe.user.dto.identity.Credential;
import com.hbvibe.user.dto.identity.TokenExchangeParam;
import com.hbvibe.user.dto.identity.UserCreationParam;
import com.hbvibe.user.dto.request.RegistrationRequest;
import com.hbvibe.user.dto.request.UpdateUserProfileRequest;
import com.hbvibe.user.dto.response.ProfileResponse;
import com.hbvibe.user.dto.response.UpdateUserProfileResponse;
import com.hbvibe.user.entity.UserAddress;
import com.hbvibe.user.entity.UserProfile;
import com.hbvibe.user.entity.UserStyleProfile;
import com.hbvibe.user.exception.AppException;
import com.hbvibe.user.exception.ErrorCode;
import com.hbvibe.user.exception.ErrorNormalizer;
import com.hbvibe.user.mapper.ProfileMapper;
import com.hbvibe.user.repository.IdentityClient;
import com.hbvibe.user.repository.ProfileRepository;
import com.hbvibe.user.repository.UserAddressRepository;
import com.hbvibe.user.repository.UserStyleRepository;
import com.hbvibe.user.service.keycloak.KeycloakGroupService;
import feign.FeignException;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ErrorNormalizer errorNormalizer;
    UserStyleRepository userStyleRepository;
    IdentityClient identityClient;
    ProfileMapper profileMapper;
    KeycloakGroupService  keycloakGroupService;
    private final UserAddressRepository userAddressRepository;
    KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;
    @Value("${idp.client-secret}")
    @NonFinal
    String clientSerect;

    public List<ProfileResponse> getAllUsers(){
        var userProfiles= profileRepository.findAll();
        return userProfiles.stream().map(profileMapper::toProfileResponse).toList();

    }

    public ProfileResponse getMyUserProfile(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(!profileRepository.existsByKeycloakId(userId)){
            throw new AppException(ErrorCode.USERID_NOT_EXISTS);
        }
        var myProfile =  profileRepository.findByKeycloakId(userId);
        return profileMapper.toProfileResponse(myProfile);
    }

    public UpdateUserProfileResponse getMyUserProfileStyle(UUID id){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(!profileRepository.existsByKeycloakId(userId)){
            throw new AppException(ErrorCode.USERID_NOT_EXISTS);
        }
        UserProfile myUserProfile = profileRepository.findByIdAndKeycloakId(id,userId)
                .orElseThrow(()->new AppException(ErrorCode.USERID_NOT_EXISTS));
        UserStyleProfile myUserStyle = userStyleRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.USERID_NOT_EXISTS));

        return profileMapper.toProfileStyleResponse(myUserProfile,myUserStyle);

    }

    public List<UpdateUserProfileResponse> getAllUserProfilesStyle(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(!profileRepository.existsByKeycloakId(userId)){
            throw new AppException(ErrorCode.USERID_NOT_EXISTS);
        }
        List<UserProfile> userProfiles= profileRepository.findAll();
        return userProfiles.stream().map(userProfile -> {
            // ví có mối quan hệ 1-1 lên khi khai báo nó tự động lâý ra dữ liệu của style
            UserStyleProfile styleProfile = userProfile.getUserStyleProfile();
            return profileMapper.toProfileStyleResponse(userProfile,styleProfile);
        }).toList();
    }

    public ProfileResponse register(RegistrationRequest registrationRequest) {
        log.info("Dữ liệu nhận từ Frontend: {}", registrationRequest);
        try {
            if (profileRepository.existsByPhoneNumber(registrationRequest.getPhoneNumber())) {
                throw new AppException(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
            }
            if(profileRepository.existsByEmail(registrationRequest.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            // create  account  in keycloak
            //Exchange client token
            var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSerect)
                    .scope("openid")
                    .build());

            log.info("TokenInfo {}", token);
            // Create user with client Token and given info
            // Get userId of keyCloak account
            var creationResponse = identityClient.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(registrationRequest.getUsername())
                            .enabled(true)
                            .email(registrationRequest.getEmail())
                            .firstName(registrationRequest.getFirstName())
                            .lastName(registrationRequest.getLastName())
                            .credentials(
                                    List.of(Credential.builder()
                                            .type("password")
                                            .value(registrationRequest.getPassword())
                                            .temporary(false)
                                            .build())
                            )
                            .build()
            );

//             publish message to kafka
            // lấy ra keycloakId(userId)
            String keyCloakId = extractUserId(creationResponse);
            log.info("keyCloakId: {}", keyCloakId);
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .userId(keyCloakId)
                    .channel(Channel.EMAIL)
                    .recipient(List.of(new Recepient(registrationRequest.getUsername()
                            ,registrationRequest.getEmail())))
                    .templateCode(1)
                    .param(Map.of(
                                    "username", registrationRequest.getUsername(),
                                    "email", registrationRequest.getEmail(),
                                    "gender", registrationRequest.getGender()
                    ))
                    .subject("Wecome to HBvibe")
                    .body("Hello " + registrationRequest.getUsername())
                    .timestamp(System.currentTimeMillis())
                    .build();
            kafkaTemplate.send("notification-delivery", notificationEvent)
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


            var userProfile= profileMapper.toUserProfile(registrationRequest);
            userProfile.setKeycloakId(keyCloakId);
            //khởi tạo bảng userProfileStyle

            userProfile= profileRepository.save(userProfile);
            keycloakGroupService.addUserGruop(keyCloakId,"CUSTOMER");
            log.info("Đã cập nhật thành công quyền Khách hàng cho tài khoản mới {}", keyCloakId);

            return profileMapper.toProfileResponse(userProfile);

        } catch (FeignException exception) {
            throw  errorNormalizer.handleKeyCloakException(exception);


        }


    }

    private String extractUserId(ResponseEntity<?> response){
        String location=response.getHeaders().get("Location").getFirst();
        String [] splitedStr = location.split("/");
        return splitedStr[splitedStr.length-1];
    }

    public UpdateUserProfileResponse updateUserProfile(UpdateUserProfileRequest updateUserProfileRequest, UUID id) {
        log.info("Dữ liệu nhận từ UserProfileRequest: {}", updateUserProfileRequest);
        log.info("ID: {}" ,id);
        if(Objects.isNull(updateUserProfileRequest)){
            throw new AppException(ErrorCode.VALUE_NULL);
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        // xem userId của token có tồn tại trong bảng UserProfile không
        if(!profileRepository.existsByKeycloakId(userId)){
            throw new AppException(ErrorCode.USERID_NOT_EXISTS);
        }
        UserProfile userProfile = profileRepository.findByIdAndKeycloakId(id,userId)
                .orElseThrow(()->new AppException(ErrorCode.USERID_NOT_EXISTS));
        userProfile.setFirstName(updateUserProfileRequest.getFirstName());
        userProfile.setLastName(updateUserProfileRequest.getLastName());
        userProfile.setEmail(updateUserProfileRequest.getEmail());
        userProfile.setBirthDate(updateUserProfileRequest.getBirthDate());
        profileRepository.save(userProfile);

        UserStyleProfile userStyleProfile = userStyleRepository.findById(id)
                .orElseGet(()-> {
                    UserStyleProfile newStyle = new UserStyleProfile();
                    newStyle.setUserProfile(userProfile);
                    return newStyle;
                });
        userStyleProfile.setHeightCm(updateUserProfileRequest.getHeightCm());
        userStyleProfile.setWeightKg(updateUserProfileRequest.getWeightKg());
        userStyleProfile.setSkinTone(updateUserProfileRequest.getSkinTone());
        userStyleProfile.setBodyShape(updateUserProfileRequest.getBodyShape());
        userStyleProfile.setFavoriteStyles(updateUserProfileRequest.getFavoriteStyles());
        userStyleProfile.setPreferredColors(updateUserProfileRequest.getPreferredColors());
        userStyleRepository.save(userStyleProfile);

        return profileMapper.toProfileStyleResponse(userProfile,userStyleProfile);

    }
    // xóa toàn bộ tài khoản (dành cho admin)
    @Transactional
    public void deleteUserProfile(UUID id) {
        UserProfile userProfile = profileRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.USERID_NOT_EXISTS));

        var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSerect)
                .scope("openid")
                .build());

        log.info("TokenInfo {}", token);
        // gọi qua keycloak để xóa user (xóa cứng)
        identityClient.deleteUser("Bearer " + token.getAccessToken(),userProfile.getKeycloakId());
        profileRepository.delete(userProfile);

    }
}