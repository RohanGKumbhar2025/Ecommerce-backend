package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.dto.OrderResponseDTO; // ✅ Import the DTO
import com.ecom.productcatalog.model.Order;
import com.ecom.productcatalog.security.UserPrincipal;
import com.ecom.productcatalog.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    // ✅ FIX: Return the DTO to prevent lazy loading exceptions
    public ResponseEntity<OrderResponseDTO> checkout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Order savedOrder = checkoutService.checkout(userPrincipal.getId());
        // Convert the saved order entity to a DTO before sending it to the client
        OrderResponseDTO orderResponse = new OrderResponseDTO(savedOrder);
        return ResponseEntity.ok(orderResponse);
    }
}