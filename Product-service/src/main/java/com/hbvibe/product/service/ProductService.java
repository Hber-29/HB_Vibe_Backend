package com.hbvibe.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbvibe.product.dto.request.ProductRequest;
import com.hbvibe.product.dto.request.UpdateProductRequest;
import com.hbvibe.product.dto.response.*;
import com.hbvibe.product.entity.*;
import com.hbvibe.product.exception.AppException;
import com.hbvibe.product.exception.ErrorCode;
import com.hbvibe.product.mapper.ProductMapper;
import com.hbvibe.product.repository.ProductRepository;

import com.hbvibe.product.repository.ProductVariantRespository;
import com.hbvibe.product.repository.category.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    CategoryRepository categoryRepository;
    ProductVariantRespository productVariantRespository;

    @Transactional
//    @PreAuthorize("hasRole('create_product_brand')")
    // annotation này vai trò là người dọn rác ,khi có sựu thay đổi dữ liệu thêm ,sửa ,xóa nó sẽ tự động xóa sachj redis để cập nhật lại
    @CacheEvict(value = "public_products", allEntries = true)
    public ProductResponse createProduct(String userId,String brandId,ProductRequest productRequest){

        checkPermission(userId,brandId,List.of("OWNER","MANAGER","STAFF"));
        Product product = Product.builder()
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

        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CANNOT_CATEGORY));
            // Gán Category vào Product.
            product.setCategories(new HashSet<>(List.of(category)));
        }

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
        // chuyển dữ liệu sang dto để có thể lưu được vào redis (vì khi lưu trực tiếp bằng entity nó đang bị vòng lặp vô tận do mối quan hệ manytomany)
        ProductResponse responseDto = productMapper.toProductResponse(productNew);
        // lưu sản phẩm vào redis để phục vụ cho chức năng lấy chi tiết sản phẩm
        try{
            String productJson = objectMapper.writeValueAsString(responseDto);
            String detailKey = String.format("product_detail:" + productNew.getSlug());
            stringRedisTemplate.opsForValue().set(detailKey,productJson,7, TimeUnit.DAYS);
            log.info("Đã lưu thành công sản phẩm vào cache redis với slug : {}",responseDto.getSlug());
        }catch(Exception e){
            log.error("Lỗi khi lưu sản phẩm vào Redis (Cache Warming): {}", e.getMessage());
        }

        return responseDto;

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
    // hàm xóa sản phẩm dành cho admin
    @Transactional
    @CacheEvict(value = "public_products" ,allEntries = true)
    public void deleteProductByAdmin(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOT_EXISTS));
        productRepository.delete(product);
        log.info("Admin đã xóa sản phẩm : {}" ,productId);
    }
    // hàm xóa sản phẩm dành cho brand
    @Transactional
    @CacheEvict(value = "public_products" ,allEntries = true)
    public void deleteProductByBrand(Long productId, String brandId ,String userId){
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOT_EXISTS));

        if(!product.getBrandId().equals(brandId)){
            throw new RuntimeException("Sản phẩm không thuộc gian hàng này !");
        }
        checkPermission(userId,brandId,List.of("OWNER","MANAGER"));
        productRepository.delete(product);
        log.info("User {} đã xóa sản phẩm {} của gian hàng {}", userId, productId, brandId);
    }


    @CacheEvict(value = "public_products", allEntries = true)
    public UpdateProductResponse updateProductDetails(Long productId,UpdateProductRequest updateProductRequest){
        Product excitingProduct = productRepository.findById(productId)
                .orElseThrow(()-> new AppException(ErrorCode.USERID_NOT_EXISTS));
        String oldSlug = excitingProduct.getSlug();


            excitingProduct.setName(updateProductRequest.getName());
            excitingProduct.setDescription(updateProductRequest.getDescription());
            excitingProduct.setShortDescription(updateProductRequest.getShortDescription());
            excitingProduct.setThumbnail(updateProductRequest.getThumbnail());
            excitingProduct.setPrice(updateProductRequest.getPrice());
            excitingProduct.setSalePrice(updateProductRequest.getSalePrice());
            excitingProduct.setStatus(updateProductRequest.getStatus());
            excitingProduct.setIsFeatured(updateProductRequest.getIsFeatured());
            excitingProduct.setMetaTitle(updateProductRequest.getMetaTitle());
            excitingProduct.setMetaDescription(updateProductRequest.getMetaDescription());

        if (updateProductRequest.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(updateProductRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CANNOT_CATEGORY));

            excitingProduct.getCategories().clear();
            excitingProduct.getCategories().add(newCategory);
        }
        String newSlug = null;
        if(updateProductRequest.getName() != null){
            newSlug = generateSlug(updateProductRequest.getName());
            excitingProduct.setSlug(newSlug);
        }
        snycVariants(excitingProduct,updateProductRequest.getVariants());
        snycImages(excitingProduct,updateProductRequest.getImages());
        Product updatedProduct = productRepository.save(excitingProduct);
        UpdateProductResponse updateProductResponse = productMapper.toUpdateProductResponse(updatedProduct);
        updateRedisCache(oldSlug,newSlug,updateProductResponse);

        return updateProductResponse;

    }
    // hàm lấy giá ,thông tin cow bản cho giửo hàng
    public VariantForCartResponse getVariantForCart(Long id){
        ProductVariant variant = productVariantRespository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOT_EXISTS));
        Boolean isActive = (variant.getStatus()== Status.ACTIVE);

        BigDecimal finalPrice;
        if(variant.getSalePrice() != null && variant.getSalePrice().compareTo(BigDecimal.ZERO)>0){
            finalPrice = variant.getSalePrice();
        }else {
            finalPrice = variant.getPrice(); // không có giá sale thì lấy giá gốc
        }
        return VariantForCartResponse.builder()
                .variantId(variant.getId())
                .price(finalPrice)
                .stockQuantity(variant.getStockQuantity())
                .isActive(isActive)
                .build();

    }

    private void snycVariants(Product product, List<UpdateProductRequest.VariantDto> variantDtos ){
        if (variantDtos == null) return;
        // logic gom tất cả các id của variants vào
        Set<ProductVariant> excitingVariants = product.getVariants();
        Set<Long> incomingIds = new HashSet<>();
        for(UpdateProductRequest.VariantDto dto : variantDtos){
            if(dto.getId()!= null){
                incomingIds.add(dto.getId());
            }
        }
        // xóa các variant không có trong request gửi đến
        excitingVariants.removeIf(variant ->
                variant.getId()!= null && !incomingIds.contains(variant.getId()));
        for(UpdateProductRequest.VariantDto dto : variantDtos){
            if(dto.getId()!= null){
                //nếu trùng id thì chỉ là chỉnh sửa tệp cũ với id cũ
                excitingVariants.stream()
                        .filter(v ->v.getId().equals(dto.getId()))
                        .findFirst()
                        .ifPresent(excitingVariant -> {
                            excitingVariant.setSize(dto.getSize());
                            excitingVariant.setColor(dto.getColor());
                            excitingVariant.setSku(dto.getSku());
                            excitingVariant.setStockQuantity(dto.getStockQuantity());
                            excitingVariant.setPrice(dto.getPrice());
                            excitingVariant.setSalePrice(dto.getSalePrice());
                            excitingVariant.setStatus(dto.getStatus());
                            excitingVariant.setWeight(dto.getWeight());
                            excitingVariant.setImage(dto.getImage());
                        });
            }
            else{
                // không trùng id thì là giá trị mới phải tạo mới với id mới
                ProductVariant newProductVariant = ProductVariant.builder()
                        .product(product)
                        .size(dto.getSize())
                        .color(dto.getColor())
                        .sku(dto.getSku())
                        .price(dto.getPrice())
                        .salePrice(dto.getSalePrice())
                        .stockQuantity(dto.getStockQuantity())
                        .weight(dto.getWeight())
                        .image(dto.getImage())
                        .status(dto.getStatus())
                        .build();
                excitingVariants.add(newProductVariant);

            }
        }

    }

    private void snycImages(Product product, List<UpdateProductRequest.ImageDto> imageDtos){
        if(imageDtos == null) return;

        Set<Long> incomingIds = new HashSet<>();
        Set<ProductImage> excitingImages = product.getImages();
        for(UpdateProductRequest.ImageDto dto : imageDtos){
            if(dto.getId()!= null){
                incomingIds.add(dto.getId());
            }
        }
        excitingImages.removeIf(image
                ->image.getId()!= null && !incomingIds.contains(image.getId()));

        for(UpdateProductRequest.ImageDto dto : imageDtos){
            if(dto.getId()!= null){
                excitingImages.stream()
                        .filter(image ->image.getId().equals(dto.getId()))
                        .findFirst()
                        .ifPresent(excitingImage -> {
                            excitingImage.setProduct(product);
                            excitingImage.setImageUrl(dto.getImageUrl());
                            excitingImage.setSortOrder(dto.getSortOrder());
                        });
            }
            else {
                ProductImage newProductImage = ProductImage.builder()
                        .product(product)
                        .altText(dto.getAltText())
                        .imageUrl(dto.getImageUrl())
                        .sortOrder(dto.getSortOrder())
                        .build();
                excitingImages.add(newProductImage);
            }
        }

    }

    private void updateRedisCache(String oldSlug , String newSlug , UpdateProductResponse productResponse){
        try {
            String oldDetailKey = "product_detail:" + oldSlug;
            String newDetailKey = "product_detail:" + newSlug;
            String oldViewKey = "product_view:" + oldSlug;
            String newViewKey = "product_view:" + newSlug;

            stringRedisTemplate.delete(oldDetailKey);
            if(!oldSlug.equals(newSlug)){
                Boolean hasOldView = stringRedisTemplate.hasKey(oldViewKey);
                if(Boolean.TRUE.equals(hasOldView)){
                    stringRedisTemplate.rename(oldViewKey, newViewKey);
                }
            }

            String jsonToCache = objectMapper.writeValueAsString(productResponse);
            stringRedisTemplate.opsForValue().set(newDetailKey, jsonToCache,7, TimeUnit.DAYS);
            log.info("Đã cập nhật Redis thành công cho sản phẩm {}", newSlug);
        }catch (Exception e){
            log.info("Lỗi khi cập nhật Redis cho sản phẩm {}", newSlug);
        }
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
