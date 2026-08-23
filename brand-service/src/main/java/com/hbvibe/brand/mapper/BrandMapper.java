package com.hbvibe.brand.mapper;

import com.hbvibe.brand.dto.response.BrandCreateResponse;
import com.hbvibe.brand.dto.response.UpdateBrandResponse;
import com.hbvibe.brand.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandCreateResponse toBrandCreateResponse(Brand brand);
    UpdateBrandResponse toUpdateBrandResponse(Brand brand);
}
