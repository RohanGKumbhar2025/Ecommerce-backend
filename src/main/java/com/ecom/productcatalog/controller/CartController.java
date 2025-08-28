package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.dto.CartItemRequest;
import com.ecom.productcatalog.dto.CartItemResponseDTO; // ✅ Import the new DTO
import com.ecom.productcatalog.model.CartItem;
import com.ecom.productcatalog.security.UserPrincipal;
import com.ecom.productcatalog.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    // ✅ FIX: Return a List of DTOs instead of entities
    public List<CartItemResponseDTO> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return cartService.getCart(userPrincipal.getId()).stream()
                .map(CartItemResponseDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/wishlist")
    // ✅ FIX: Return a List of DTOs instead of entities
    public List<CartItemResponseDTO> getWishlist(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return cartService.getWishlist(userPrincipal.getId()).stream()
                .map(CartItemResponseDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    // ✅ FIX: Return the DTO after adding/updating an item
    public CartItemResponseDTO addToCartOrWishlist(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody CartItemRequest cartItemRequest) {
        CartItem updatedItem = cartService.addToCartOrWishlist(userPrincipal.getId(), cartItemRequest);
        return new CartItemResponseDTO(updatedItem);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromCartOrWishlist(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long productId) {
        cartService.removeFromCartOrWishlist(userPrincipal.getId(), productId);
        return ResponseEntity.noContent().build();
    }
}