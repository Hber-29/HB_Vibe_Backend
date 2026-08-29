package com.hbvibe.product.repository;

import com.hbvibe.product.entity.Product;
import com.hbvibe.product.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByStatus(Status status, Pageable pageable);
    Page<Product> findByBrandIdAndStatus(String brandId, Status status, Pageable pageable);
    // Báo cho Hibernate biết: "Khi lấy Product, hãy JOIN lấy luôn cả mảng Variants và Images lên trong 1 câu SQL duy nhất"
    @EntityGraph(attributePaths = {"variants", "images"})
    Optional<Product> findBySlug(String slug);
    Optional<Product> findById(Long id);

}
