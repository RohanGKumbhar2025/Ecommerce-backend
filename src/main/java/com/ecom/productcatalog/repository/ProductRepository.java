package com.ecom.productcatalog.repository;

import com.ecom.productcatalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    // ✅ FIX: This query is simplified to prevent the 500 Internal Server Error on Render.
    // It removes 'JOIN FETCH' which can conflict with pagination and complex WHERE clauses.
    @Query(value = "SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:searchTerm IS NULL OR lower(p.name) LIKE lower(concat('%', :searchTerm, '%'))) AND " +
            "(:isNew IS NULL OR p.isNew = :isNew) AND " +
            "(:onSale IS NULL OR p.onSale = :onSale)",
            countQuery = "SELECT count(p) FROM Product p WHERE " +
                    "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
                    "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
                    "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
                    "(:searchTerm IS NULL OR lower(p.name) LIKE lower(concat('%', :searchTerm, '%'))) AND " +
                    "(:isNew IS NULL OR p.isNew = :isNew) AND " +
                    "(:onSale IS NULL OR p.onSale = :onSale)")
    Page<Product> findWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("searchTerm") String searchTerm,
            @Param("isNew") Boolean isNew,
            @Param("onSale") Boolean onSale,
            Pageable pageable
    );
}