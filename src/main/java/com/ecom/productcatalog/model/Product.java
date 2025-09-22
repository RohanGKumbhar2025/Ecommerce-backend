package com.ecom.productcatalog.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "product", indexes = {
        @Index(name = "idx_product_category_id", columnList = "category_id"),
        @Index(name = "idx_product_rating", columnList = "rating"),
        @Index(name = "idx_product_name", columnList = "name")
})
@Data
@SQLDelete(sql = "UPDATE product SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ CRITICAL FIX: Explicitly define the column type as 'text'.
    // This resolves the "function lower(bytea) does not exist" error by ensuring
    // the database column is a proper string type, not a binary type.
    @Column(columnDefinition = "text")
    private String name;

    // ✅ BEST PRACTICE: Also define the description as 'text' to prevent future issues.
    @Column(columnDefinition = "text")
    private String description;

    private String imageUrl;
    private Double price;
    private Boolean inStock;
    private Double originalPrice;
    private Double rating;
    private Integer reviews;
    private Boolean isNew;
    private Boolean onSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private boolean deleted = false;
}