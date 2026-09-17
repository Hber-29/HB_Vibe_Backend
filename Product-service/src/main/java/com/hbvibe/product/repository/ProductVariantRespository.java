package com.hbvibe.product.repository;

import com.hbvibe.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRespository extends JpaRepository<ProductVariant, Long> {
    @Override
    Optional<ProductVariant> findById(Long id);
}
