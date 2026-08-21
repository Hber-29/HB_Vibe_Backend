package com.hbvibe.brand.service;

import com.hbvibe.brand.dto.request.BrandCreateRequest;
import com.hbvibe.brand.dto.response.BrandCreateResponse;
import com.hbvibe.brand.entity.Brand;
import com.hbvibe.brand.entity.BrandMember;
import com.hbvibe.brand.entity.BrandRole;
import com.hbvibe.brand.entity.BrandStatus;
import com.hbvibe.brand.exception.AppException;
import com.hbvibe.brand.exception.ErrorCode;
import com.hbvibe.brand.mapper.BrandMapper;
import com.hbvibe.brand.repository.BrandMemberRepository;
import com.hbvibe.brand.repository.BrandRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
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


    @Transactional
    public BrandCreateResponse createBrand(BrandCreateRequest brandCreateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        if (brandRepository.existsByName(brandCreateRequest.getName())) {
            throw new AppException(ErrorCode.BRAND_NAME_EXITED);
        }
        String generatedSlug = generateSlug(brandCreateRequest.getName());
        // nếu name nghĩa khác nhưng khi bỏ dấu lại giống nha vè slug thfi sẽ thêm 1 mã uuid để phân biệt
        if(brandRepository.existsBySlug(generatedSlug)) {
            generatedSlug = generatedSlug + "-" + UUID.randomUUID().toString().substring(0, 5);
        }

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

        return brandMapper.toBrandCreateResponse(brand);


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


}
