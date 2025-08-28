// productcatalog/src/main/java/com/ecom/productcatalog/repository/OrderRepository.java
package com.ecom.productcatalog.repository;

import com.ecom.productcatalog.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}