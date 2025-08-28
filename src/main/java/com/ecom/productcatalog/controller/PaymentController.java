package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.model.Order;
import com.ecom.productcatalog.repository.CartItemRepository; // Import CartItemRepository
import com.ecom.productcatalog.repository.OrderRepository;
import com.ecom.productcatalog.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional; // Use Spring's Transactional
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository; // Inject CartItemRepository

    public PaymentController(OrderRepository orderRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository; // Initialize it
    }

    @PostMapping("/confirm/{orderId}")
    @Transactional // Use Spring's Transactional for database operations
    public ResponseEntity<?> confirmPayment(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        // Security check: Verify the order belongs to the logged-in user
        if (!Objects.equals(order.getUser().getId(), userPrincipal.getId())) {
            return ResponseEntity.status(403).body("Forbidden: You do not have permission to confirm this order.");
        }

        // 1. Update the order status to "COMPLETED"
        order.setStatus("COMPLETED");
        orderRepository.save(order);

        // ✅ FIX: Clear the user's cart from the database only AFTER payment is confirmed.
        cartItemRepository.deleteByUserIdAndIsWishlisted(userPrincipal.getId(), false);

        return ResponseEntity.ok("Payment confirmed and order status updated.");
    }
}