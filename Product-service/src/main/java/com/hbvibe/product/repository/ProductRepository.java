package com.hbvibe.product.repository;

import com.hbvibe.product.entity.Product;
import com.hbvibe.product.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByStatus(Status status, Pageable pageable);

}
