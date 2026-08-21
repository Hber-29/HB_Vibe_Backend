package com.hbvibe.brand.repository;

import com.hbvibe.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand,String> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);

}
