package com.hbvibe.banner.repository;

import com.hbvibe.banner.entity.Banner;
import com.hbvibe.banner.entity.BannerPosition;
import com.hbvibe.banner.entity.BannerStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<Banner,Long> {
    @Override
    Optional<Banner> findById(Long id);

    // Lấy danh sách cho Frontend: Lọc theo Vị trí + Trạng thái ACTIVE + Sắp xếp + Giới hạn số lượng
    List<Banner> findByPositionAndStatusOrderBySortOrderAsc(BannerPosition position, BannerStatus status, Pageable pageable);
    // Kiểm tra xem Tiêu đề đã tồn tại trên toàn hệ thống chưa
    boolean existsByTitle(String title);
    // Kiểm tra xem tại Vị trí đó, Số thứ tự đó đã có ai chiếm chưa
    boolean existsByPositionAndSortOrder(BannerPosition position, Integer sortOrder);

    // 2 hàm kiểm tra đầu vào cho chức năng update(khi so sánh nó sẽ bỏ qua chính nó )
    boolean existsByTitleAndIdNot(String title, Long id);
    boolean existsByPositionAndSortOrderAndIdNot(BannerPosition position, Integer sortOrder, Long id);
}
