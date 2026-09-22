package com.hbvibe.brand.repository;

import com.hbvibe.brand.entity.Brand;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand,String> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    Optional<Brand> findById(String brandId);
    // Tìm Brand thông qua bảng BrandMember
    @Query("SELECT bm.brand FROM BrandMember bm WHERE bm.userId = :userId")
    List<Brand> findBrandsByUserId(@Param("userId") String userId);

}
