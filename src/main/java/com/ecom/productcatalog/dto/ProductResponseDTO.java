package com.ecom.productcatalog.dto;

import com.ecom.productcatalog.model.Product;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Boolean inStock;
    private Double originalPrice;
    private Double rating;
    private Integer reviews;
    private Boolean isNew;
    private Boolean onSale;
    private String categoryName;

    public ProductResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.price = product.getPrice();
        this.inStock = product.getInStock();
        this.originalPrice = product.getOriginalPrice();
        this.rating = product.getRating();
        this.reviews = product.getReviews();
        this.isNew = product.getIsNew();
        this.onSale = product.getOnSale();
        if (product.getCategory() != null) {
            this.categoryName = product.getCategory().getName();
        }
    }
}