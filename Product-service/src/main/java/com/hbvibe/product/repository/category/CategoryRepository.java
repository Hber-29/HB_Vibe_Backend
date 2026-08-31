package com.hbvibe.product.repository.category;

import com.hbvibe.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Hàm dùng để kiểm tra xem đường dẫn (slug) đã tồn tại hay chưa
    boolean existsBySlug(String slug);

    @Override
    Optional<Category> findById(Long id);
}
