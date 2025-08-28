package com.ecom.productcatalog.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
// ✅ Both the table name and the index are correctly defined here
@Table(name = "product", indexes = {
        @Index(name = "idx_product_category_id", columnList = "category_id")
})
@Data
@SQLDelete(sql = "UPDATE product SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne(fetch = FetchType.LAZY) // Using LAZY fetch is often better for performance
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private boolean deleted = false;
}