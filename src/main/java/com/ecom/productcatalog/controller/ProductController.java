package com.ecom.productcatalog.controller;

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
            @RequestParam(defaultValue = "rating-desc") String sort) {

        try {
            logger.info("Public products endpoint called with params: categoryId={}, minPrice={}, maxPrice={}, searchTerm={}, isNew={}, onSale={}, page={}, size={}, sort={}",
                    categoryId, minPrice, maxPrice, searchTerm, isNew, onSale, page, size, sort);

            String[] sortParams = sort.split("-");
            String sortField = sortParams.length > 0 ? sortParams[0] : "id";
            String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

            // ✅ CRITICAL FIX: Validate and map frontend sort fields to actual entity properties
            // This prevents crashes when an invalid sort parameter is provided.
            if (!isValidSortField(sortField)) {
                logger.warn("Invalid sort field provided: '{}'. Defaulting to 'id'.", sortField);
                sortField = "id";
            }

            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

            Page<Product> products = productService.getAllProducts(categoryId, minPrice, maxPrice, searchTerm, isNew, onSale, pageable);
            Page<ProductResponseDTO> response = products.map(ProductResponseDTO::new);

            logger.info("Successfully returned {} products out of {} total", response.getContent().size(), response.getTotalElements());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("An error occurred in getAllProducts endpoint", e);
            // Return a 500 Internal Server Error response if something goes wrong
            return ResponseEntity.status(500).body(Page.empty());
        }
    }

    // ✅ FIX: Added a helper method to validate sort fields against the Product entity
    private boolean isValidSortField(String sortField) {
        // Only allow sorting by actual fields in the Product entity to prevent errors.
        Set<String> validFields = Set.of("id", "name", "price", "rating", "reviews", "isNew", "onSale");
        return validFields.contains(sortField);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(new ProductResponseDTO(product)))
                .orElse(ResponseEntity.notFound().build());
    }
}