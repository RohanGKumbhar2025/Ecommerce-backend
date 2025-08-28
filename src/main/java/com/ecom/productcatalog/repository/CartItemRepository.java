// productcatalog/src/main/java/com/ecom/productcatalog/repository/CartItemRepository.java

package com.ecom.productcatalog.repository;

import com.ecom.productcatalog.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    List<CartItem> findByUserIdAndIsWishlisted(Long userId, Boolean isWishlisted);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserId(Long userId);
    void deleteByUserIdAndIsWishlisted(Long id, boolean b);
    void deleteByProductId(Long productId); // ✅ ADD THIS METHOD
}