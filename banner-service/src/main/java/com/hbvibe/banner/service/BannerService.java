package com.hbvibe.banner.service;

import com.hbvibe.banner.dto.request.BannerCreateRequest;
import com.hbvibe.banner.dto.request.BannerUpdateResquest;
import com.hbvibe.banner.dto.response.BannerResponse;
import com.hbvibe.banner.entity.Banner;
import com.hbvibe.banner.entity.BannerPosition;
import com.hbvibe.banner.entity.BannerStatus;
import com.hbvibe.banner.exception.AppException;
import com.hbvibe.banner.exception.ErrorCode;
import com.hbvibe.banner.mapper.BannerMapper;
import com.hbvibe.banner.repository.BannerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class BannerService {
    BannerRepository bannerRepository;
    BannerMapper bannerMapper;
    // hàm tạo banner
    public BannerResponse createBanner (BannerCreateRequest bannerCreateRequest) {
        // kiểm tra xem có bị trùng title không
        if(bannerRepository.existsByTitle(bannerCreateRequest.getTitle())) {
            throw new AppException(ErrorCode.BANNER_TITLE_EXISTED);
        }
        //kiểm tra xem ở cùng 1 póition xem có cùng sortorder hay không
        if(bannerRepository.existsByPositionAndSortOrder(bannerCreateRequest.getPosition()
                , bannerCreateRequest.getSortOrder())) {
            throw new AppException(ErrorCode.BANNER_SORT_ORDER_EXISTED);
        }
        Banner banner = Banner.builder()
                .title(bannerCreateRequest.getTitle())
                .image(bannerCreateRequest.getImage())
                .link(bannerCreateRequest.getLink())
                .sortOrder(bannerCreateRequest.getSortOrder())
                .status(bannerCreateRequest.getStatus())
                .position(bannerCreateRequest.getPosition())
                .build();
        bannerRepository.save(banner);
        return bannerMapper.toBannerResponse(banner);

    }
    // hàm chỉnh sửa banner
    public BannerResponse updateBanner(BannerUpdateResquest resquest,long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.BANNER_NOT_EXITED));
        if(bannerRepository.existsByTitleAndIdNot(resquest.getTitle(),id)) {
            throw new AppException(ErrorCode.BANNER_TITLE_EXISTED);
        }
        if(bannerRepository.existsByPositionAndSortOrderAndIdNot(resquest.getPosition()
                , resquest.getSortOrder(),id)) {
            throw new AppException(ErrorCode.BANNER_SORT_ORDER_EXISTED);
        }

        banner.setTitle(resquest.getTitle());
        banner.setImage(resquest.getImage());
        banner.setLink(resquest.getLink());
        banner.setSortOrder(resquest.getSortOrder());
        banner.setStatus(resquest.getStatus());
        banner.setPosition(resquest.getPosition());

        Banner updatedBanner= bannerRepository.saveAndFlush(banner);
        return bannerMapper.toBannerResponse(updatedBanner);
    }

    // hàm dành cho admin để lấy 1 bản ghi để đẩy lên Form edit
    public BannerResponse getBannerById(long id){
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.BANNER_NOT_EXITED));
        return bannerMapper.toBannerResponse(banner);
    }
    // dùng để lấy banner theo số lượng yêu cầu và theo position
    public List<BannerResponse> getPublicBanners(BannerPosition bannerPosition,Integer limit){
        // dùng để config sắp xếp theo từ bé đến lớn
        Sort sortConfig = Sort.by("sortOrder").ascending();
        Pageable pageable = PageRequest.of(0, limit, sortConfig);

        List<Banner> banners = bannerRepository.findByPositionAndStatusOrderBySortOrderAsc(
                bannerPosition, BannerStatus.ACTIVE,pageable);
        return banners.stream()
                .map(bannerMapper::toBannerResponse)
                .toList();
    }

    public void deleteBanner(long id){
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.BANNER_NOT_EXITED));
        bannerRepository.delete(banner);
    }

}
