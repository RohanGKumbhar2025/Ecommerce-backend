package com.ecom.productcatalog.service;

import com.ecom.productcatalog.model.Order;
import com.ecom.productcatalog.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        // In a real application, you might want to sort this by date
        return orderRepository.findAll();
    }
}