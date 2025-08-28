// productcatalog/src/main/java/com/ecom/productcatalog/controller/AdminController.java

package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.dto.OrderResponseDTO;
import com.ecom.productcatalog.dto.ProductRequest;
import com.ecom.productcatalog.dto.ProductResponseDTO; // Ensure this is imported
import com.ecom.productcatalog.model.Order;
import com.ecom.productcatalog.model.Product;
import com.ecom.productcatalog.service.OrderService;
import com.ecom.productcatalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts().stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders().stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/products")
    // ✅ FINAL FIX #1: Change the return type to the DTO
    public ResponseEntity<ProductResponseDTO> addProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product newProduct = productService.addProduct(productRequest);
        // Convert the saved entity to a DTO before sending the response
        return ResponseEntity.ok(new ProductResponseDTO(newProduct));
    }

    @PutMapping("/products/{id}")
    // ✅ FINAL FIX #2: Change the return type to the DTO
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        Product updatedProduct = productService.updateProduct(id, productRequest);
        // Convert the saved entity to a DTO before sending the response
        return ResponseEntity.ok(new ProductResponseDTO(updatedProduct));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}