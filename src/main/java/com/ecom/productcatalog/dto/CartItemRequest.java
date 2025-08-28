package com.ecom.productcatalog.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long productId;
    private Integer quantity;
    private Boolean isWishlisted;
}