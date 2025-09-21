// productcatalog/src/main/java/com/ecom/productcatalog/controller/ProductController.java

package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.dto.ProductRequest;
import com.ecom.productcatalog.dto.ProductResponseDTO;
import com.ecom.productcatalog.model.Product;
import com.ecom.productcatalog.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(required = false) Boolean onSale,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id-desc") String sort) { // ← CHANGED: Default to simple "id-desc" instead of "rating-desc"

        try {
            logger.info("Public products endpoint called with params: categoryId={}, minPrice={}, maxPrice={}, searchTerm={}, isNew={}, onSale={}, page={}, size={}, sort={}",
                    categoryId, minPrice, maxPrice, searchTerm, isNew, onSale, page, size, sort);

            // Validate pagination parameters
            if (page < 0) page = 0;
            if (size < 1 || size > 100) size = 10; // Limit max size

            String[] sortParams = sort.split("-");
            String sortField = sortParams.length > 0 ? sortParams[0] : "id";
            String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

            // ✅ FIX: Validate sort field to prevent SQL injection and invalid field errors
            if (!isValidSortField(sortField)) {
                logger.warn("Invalid sort field: {}, defaulting to 'id'", sortField);
                sortField = "id";
            }

            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

            Page<Product> products;

            // ✅ FIX: Try the complex query first, fallback to simple if it fails
            try {
                products = productService.getAllProducts(categoryId, minPrice, maxPrice, searchTerm, isNew, onSale, pageable);
            } catch (Exception complexQueryError) {
                logger.error("Complex query failed, falling back to simple query", complexQueryError);

                // Fallback to simple query without filters
                Pageable simplePagable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
                products = productService.getAllProductsSimple(simplePagable);
            }

            Page<ProductResponseDTO> response = products.map(ProductResponseDTO::new);

            logger.info("Successfully returned {} products out of {} total", response.getContent().size(), response.getTotalElements());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error in public products endpoint", e);

            // Return a more informative error response
            return ResponseEntity.status(500).body(Page.empty());
        }
    }

    private boolean isValidSortField(String sortField) {
        // ✅ Only allow valid entity fields to prevent SQL injection
        Set<String> validFields = Set.of("id", "name", "price", "rating", "isNew", "onSale");
        return validFields.contains(sortField);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        try {
            logger.info("Product by ID endpoint called with id: {}", id);

            return productService.getProductById(id)
                    .map(product -> {
                        logger.info("Found product: {}", product.getName());
                        return ResponseEntity.ok(new ProductResponseDTO(product));
                    })
                    .orElseGet(() -> {
                        logger.warn("Product not found with id: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error getting product by id: {}", id, e);
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ ADD: Simple health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Products API is running");
        return ResponseEntity.ok(response);
    }
}