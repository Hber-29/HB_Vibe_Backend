package com.hbvibe.banner.mapper;

import com.hbvibe.banner.dto.response.BannerResponse;
import com.hbvibe.banner.entity.Banner;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BannerMapper {
    BannerResponse toBannerResponse(Banner banner);
}
