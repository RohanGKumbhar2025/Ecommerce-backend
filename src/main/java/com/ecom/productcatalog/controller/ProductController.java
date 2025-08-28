// productcatalog/src/main/java/com/ecom/productcatalog/controller/ProductController.java

package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.dto.ProductRequest;
import com.ecom.productcatalog.dto.ProductResponseDTO;
import com.ecom.productcatalog.model.Product;
import com.ecom.productcatalog.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductResponseDTO> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String searchTerm,
            // ✅ Add the new boolean parameters to the controller method
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(required = false) Boolean onSale,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating-desc") String sort) {

        String[] sortParams = sort.split("-");
        String sortField = sortParams.length > 0 ? sortParams[0] : "rating";
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // ✅ Pass the new parameters to the service layer
        return productService.getAllProducts(categoryId, minPrice, maxPrice, searchTerm, isNew, onSale, pageable)
                .map(ProductResponseDTO::new);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(new ProductResponseDTO(product)))
                .orElse(ResponseEntity.notFound().build());
    }
}