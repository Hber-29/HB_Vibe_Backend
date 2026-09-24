package com.hbvibe.cart.repository;

import com.hbvibe.cart.entity.CartItem;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findById(Long id);
    Optional<CartItem> findByCartIdAndProductVariantId(Long cartId, Long productVariantId);
    // Dùng COUNT(ci.id) để đếm xem có bao nhiêu DÒNG (loại mặt hàng) trong giỏ
    @Query("SELECT COUNT(ci.id) FROM CartItem ci WHERE ci.cart.userId = :userId")
    Integer countTotalItemsInCart(@Param("userId") String userId);
    // đùng để lấy danh sách các sản phẩm có isSelected = true để phục vụ cho chức năng order .
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.userId = :userId AND ci.isSelected = true")
    List<CartItem> findSelectedItemsByUserId(@Param("userId") String userId);
}
