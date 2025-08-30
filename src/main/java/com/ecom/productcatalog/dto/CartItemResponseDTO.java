package com.ecom.productcatalog.dto;

import com.ecom.productcatalog.model.CartItem;
import lombok.Data;

@Data
public class CartItemResponseDTO {
    private Long productId;
    private String name;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Boolean isWishlisted;
    private Boolean inStock; // ✅ ADDED

    public CartItemResponseDTO(CartItem cartItem) {
        this.productId = cartItem.getProduct().getId();
        this.name = cartItem.getProduct().getName();
        this.imageUrl = cartItem.getProduct().getImageUrl();
        this.price = cartItem.getProduct().getPrice();
        this.quantity = cartItem.getQuantity();
        this.isWishlisted = cartItem.getIsWishlisted();
        this.inStock = cartItem.getProduct().getInStock(); // ✅ ADDED
    }
}