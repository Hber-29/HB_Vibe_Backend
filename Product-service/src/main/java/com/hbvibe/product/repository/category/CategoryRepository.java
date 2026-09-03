package com.hbvibe.product.repository.category;

import com.hbvibe.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Hàm dùng để kiểm tra xem đường dẫn (slug) đã tồn tại hay chưa
    boolean existsBySlug(String slug);
    @Override
    Optional<Category> findById(Long id);
    // Lấy danh mục gốc, sắp xếp theo sortOrder tăng dần, nếu trùng thì xếp theo tên A-Z
    List<Category> findByParentIsNullOrderBySortOrderAscNameAsc();
    // kiểm tra slug nhưng sẽ bỏ qua cái slug của id đang update
    boolean existsBySlugAndIdNot(String slug, Long id);
}
