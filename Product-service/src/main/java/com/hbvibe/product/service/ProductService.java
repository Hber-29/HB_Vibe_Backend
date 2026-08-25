package com.hbvibe.product.service;

import com.hbvibe.product.dto.ApiResponse;
import com.hbvibe.product.dto.request.ProductRequest;
import com.hbvibe.product.dto.response.PageResponse;
import com.hbvibe.product.dto.response.ProductListResponse;
import com.hbvibe.product.dto.response.ProductResponse;
import com.hbvibe.product.entity.Product;
import com.hbvibe.product.entity.ProductImage;
import com.hbvibe.product.entity.ProductVariant;
import com.hbvibe.product.entity.Status;
import com.hbvibe.product.exception.AppException;
import com.hbvibe.product.exception.ErrorCode;
import com.hbvibe.product.mapper.ProductMapper;
import com.hbvibe.product.repository.ProductRepository;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;


import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    @PreAuthorize("hasRole('create_product_brand')")
    public ProductResponse createProduct(String userId,String brandId,ProductRequest productRequest){

        checkPermission(userId,brandId,List.of("OWNER","MANAGER","STAFF"));
        Product product = Product.builder()
                .categoryId(productRequest.getCategoryId())
                .brandId(brandId)
                .name(productRequest.getName())
                .slug(generateSlug(productRequest.getName()))
                .shortDescription(productRequest.getShortDescription())
                .description(productRequest.getDescription())
                .thumbnail(productRequest.getThumbnail())
                .price(productRequest.getPrice())
                .salePrice(productRequest.getSalePrice())
                .status(productRequest.getStatus()!= null ? productRequest.getStatus() : Status.ACTIVE)
                .isFeatured(productRequest.getIsFeatured()!=null ? productRequest.getIsFeatured():false)
                .viewCount(0)
                .metaTitle(productRequest.getMetaTitle())
                .metaDescription(productRequest.getMetaDescription())
                .build();

        if(productRequest.getImages()!=null && !productRequest.getImages().isEmpty()){
            var images = productRequest.getImages().stream().map(img -> ProductImage.builder()
                    .product(product)
                    .imageUrl(img.getImageUrl())
                    .sortOrder(img.getSortOrder())
                    .altText(img.getAltText())
                    .build())
                    .collect(Collectors.toList());
            product.setImages(images);
        }
        if(productRequest.getVariants()!=null && !productRequest.getVariants().isEmpty()){
            var variants = productRequest.getVariants().stream().map(variant -> ProductVariant.builder()
                            .product(product)
                            .size(variant.getSize())
                            .color(variant.getColor())
                            .sku(variant.getSku())
                            .stockQuantity(variant.getStockQuantity())
                            .price(variant.getPrice())
                            .salePrice(variant.getSalePrice())
                            .weight(variant.getWeight())
                            .status(Status.ACTIVE)
                    .build())
                    .collect(Collectors.toList());
            product.setVariants(variants);
        }
        var productNew = productRepository.save(product);
        return productMapper.toProductResponse(productNew);




    }


    // Hàm tiện ích tạo Slug chuẩn SEO (VD: "Áo sơ mi nam" -> "ao-so-mi-nam")
    private String generateSlug(String input) {
        if (input == null || input.isEmpty()) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("").toLowerCase();
        return slug.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
    }

    // Cache lại đúng đối tượng PageResponse sạch sẽ
    @Cacheable(value = "public_products", key = "#page + '-' + #size")
    public PageResponse<ProductListResponse> getAllProducts(int page,int size) {
        // nếu hiển thi dòng này cho thấy redis đàn trống
        log.info("Đang truy vấn database PostgreSQL (Lần đầu hoặc bị xóa Cache)");
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"createdAt"));
        Page<Product> productPage = productRepository.findByStatus(Status.ACTIVE,pageable);

        List<ProductListResponse> productList = productPage.getContent().stream()
                .map(product->ProductListResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .thumbnail(product.getThumbnail())
                        .price(product.getPrice())
                        .salePrice(product.getSalePrice())
                        .viewCount(product.getViewCount())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.<ProductListResponse>builder()
                .currentPage(productPage.getNumber())
                .totalPages(productPage.getTotalPages())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .items(productList)
                .build();
    }
    @Cacheable(value = "brand_products", key="#brandId + '-' + #page + '-' + #size")
    public PageResponse<ProductListResponse> getAllProductsByBrand(int page,int size,String brandId) {
        log.info("Đang truy vấn DB lấy sản phẩm cho Brand [{}] (Lần đầu hoặc bị xóa Cache)", brandId);
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"createdAt"));
        Page<Product> productPage = productRepository.findByBrandIdAndStatus(brandId,Status.ACTIVE,pageable);
        List<ProductListResponse> productList = productPage.getContent().stream()
                .map(product ->ProductListResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .thumbnail(product.getThumbnail())
                        .price(product.getPrice())
                        .salePrice(product.getSalePrice())
                        .viewCount(product.getViewCount())
                        .build())
                .collect(Collectors.toList());
        return PageResponse.<ProductListResponse>builder()
                .currentPage(productPage.getNumber())
                .totalPages(productPage.getTotalPages())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .items(productList)
                .build();
    }
    // hàm check quyền thông qua redis
    private void checkPermission (String userId,String brandId, List<String> allowedRoles){
        String redisKey = String.format("brand_role:%s:%s", userId, brandId);
        String currentRole = stringRedisTemplate.opsForValue().get(redisKey);
        if(currentRole==null){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if(!allowedRoles.contains(currentRole)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
