package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.dto.ProductRequest;
import com.ecom.productcatalog.dto.ProductResponseDTO;
import com.ecom.productcatalog.service.ProductService;
// Other imports...
import com.ecom.productcatalog.dto.OrderResponseDTO;
import com.ecom.productcatalog.model.Product;
import com.ecom.productcatalog.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // This is the corrected method that matches the service and what the frontend expects.
    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ProductResponseDTO> products = productService.getAllProducts(null, null, null, null, null, null, pageable)
                .map(ProductResponseDTO::new);

        return ResponseEntity.ok(products);
    }

    // ... (rest of the file is unchanged)
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders().stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> addProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product newProduct = productService.addProduct(productRequest);
        return ResponseEntity.ok(new ProductResponseDTO(newProduct));
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        Product updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(new ProductResponseDTO(updatedProduct));
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}