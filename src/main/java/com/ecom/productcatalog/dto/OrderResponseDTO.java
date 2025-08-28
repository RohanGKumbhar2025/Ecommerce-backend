package com.ecom.productcatalog.dto;

import com.ecom.productcatalog.model.Order;
import com.ecom.productcatalog.model.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponseDTO {
    private Long id;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;
    private List<OrderItemResponseDTO> orderItems;
    private String userName;

    public OrderResponseDTO(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponseDTO::new)
                .collect(Collectors.toList());
        if (order.getUser() != null) {
            this.userName = order.getUser().getName(); // ✅ Populate the user's name
        }
    }
}

@Data
class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private Double price;

    public OrderItemResponseDTO(OrderItem orderItem) {
        this.productId = orderItem.getProduct().getId();
        this.productName = orderItem.getProduct().getName();
        this.imageUrl = orderItem.getProduct().getImageUrl();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
    }
}