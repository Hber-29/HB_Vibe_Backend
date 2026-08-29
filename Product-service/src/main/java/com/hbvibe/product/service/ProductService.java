package com.hbvibe.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    final StringRedisTemplate stringRedisTemplate;
    ObjectMapper objectMapper;

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
                    .collect(Collectors.toSet());
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
                    .collect(Collectors.toSet());
            product.setVariants(variants);
        }
        var productNew = productRepository.save(product);
        // lưu sản phẩm vào redis để phục vụ cho chức năng lấy chi tiết sản phẩm
        try{
            String productJson = objectMapper.writeValueAsString(productNew);
            String detailKey = String.format("product_detail:" + productNew.getSlug());
            stringRedisTemplate.opsForValue().set(detailKey,productJson,7, TimeUnit.DAYS);
            log.info("Đã lưu thành công sản phẩm vào cache redis với slug : {}",productNew.getSlug());
        }catch(Exception e){
            log.error("Lỗi khi lưu sản phẩm vào Redis (Cache Warming): {}", e.getMessage());
        }

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
                        .slug(product.getSlug())
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
    // hàm lấy thông tin chi tiết sản phẩm
    @Transactional
    public ProductResponse getProductDetails(String slug){
        String detailKey = "product_detail:" + slug;
        String viewKey = "product_view:" + slug;
        ProductResponse productResponse = null;

        try{
            String productJson = stringRedisTemplate.opsForValue().get(detailKey);
            if(productJson != null){
                productResponse = objectMapper.readValue(productJson, ProductResponse.class);
                log.info("Lấy dữ liệu thành công từ redis cho slug: {}" , slug);

            }
        }catch(Exception e){
            log.info("Lỗi không lấy được dữ liệu trong redis: {} " ,e.getMessage() );
        }
        if(productResponse == null){
            log.info("Cache đang rỗng ! đang lấy databse để lưu vào redis với slug : {}", slug);
            Product product= productRepository.findBySlug(slug)
                    .orElseThrow(()-> new AppException(ErrorCode.USERID_NOT_EXISTS));
            productResponse = productMapper.toProductResponse(product);

            try{
                String productJson = objectMapper.writeValueAsString(productResponse);
                stringRedisTemplate.opsForValue().set(detailKey,productJson,7,TimeUnit.DAYS);
                log.info("Lưu thành công dữ liệu của slug {} vào redis ", slug);
            }catch(Exception e){
                log.info("Lỗi khônh lưu được dữ liệu trong redis: {}" , e.getMessage() );
            }

        }
        // logic giúp tăng view(cập nhậ trong redis) khi người dùng ấn vaof xem chi tiết
        try{
            Boolean hasViewKey = stringRedisTemplate.hasKey(viewKey);
            if(Boolean.FALSE.equals(hasViewKey)){
                stringRedisTemplate.opsForValue().set(viewKey,String.valueOf(productResponse.getViewCount()));
            }
            Long currentView = stringRedisTemplate.opsForValue().increment(viewKey);

            if(currentView!=null){
                productResponse.setViewCount(currentView.intValue());
            }
        }catch(Exception e){
            log.info("Lỗi khi cập nhật view trong redis: {}" ,e.getMessage() );
            productResponse.setViewCount(productResponse.getViewCount()+1);
        }

        return productResponse;



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
