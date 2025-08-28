package com.ecom.productcatalog.service;

import com.ecom.productcatalog.model.*;
import com.ecom.productcatalog.repository.CartItemRepository;
import com.ecom.productcatalog.repository.OrderRepository;
import com.ecom.productcatalog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public CheckoutService(CartItemRepository cartItemRepository, UserRepository userRepository, OrderRepository orderRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order checkout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUserIdAndIsWishlisted(user.getId(), false);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        double totalAmount = 0.0;

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            totalAmount += cartItem.getQuantity() * cartItem.getProduct().getPrice();
        }

        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }
}