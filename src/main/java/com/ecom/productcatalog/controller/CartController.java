package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.dto.CartItemRequest;
import com.ecom.productcatalog.dto.CartItemResponseDTO;
import com.ecom.productcatalog.dto.ProductResponseDTO; // ✅ Import Product DTO
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
    public List<CartItemResponseDTO> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return cartService.getCart(userPrincipal.getId()).stream()
                .map(CartItemResponseDTO::new)
                .collect(Collectors.toList());
    }

    // ✅ MODIFIED: This endpoint is now highly efficient
    @GetMapping("/wishlist")
    public List<ProductResponseDTO> getWishlist(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return cartService.getWishlistProducts(userPrincipal.getId()).stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping
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
