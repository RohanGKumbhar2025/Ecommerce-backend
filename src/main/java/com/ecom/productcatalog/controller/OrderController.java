package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.dto.OrderResponseDTO;
import com.ecom.productcatalog.model.Order;
import com.ecom.productcatalog.repository.OrderRepository;
import com.ecom.productcatalog.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<OrderResponseDTO> getUserOrders(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return orderRepository.findByUserId(userPrincipal.getId()).stream()
                .map(OrderResponseDTO::new) // ✅ Convert List<Order> to List<OrderResponseDTO>
                .collect(Collectors.toList());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        if (!Objects.equals(order.getUser().getId(), userPrincipal.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(new OrderResponseDTO(order)); // ✅ Convert Order to DTO
    }
}