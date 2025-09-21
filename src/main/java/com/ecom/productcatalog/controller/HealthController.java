package com.ecom.productcatalog.controller;

import com.ecom.productcatalog.model.Product;
import com.ecom.productcatalog.repository.CategoryRepository;
import com.ecom.productcatalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Test database connection
            long productCount = productRepository.count();
            long categoryCount = categoryRepository.count();

            response.put("status", "UP");
            response.put("database", "Connected");
            response.put("productCount", productCount);
            response.put("categoryCount", categoryCount);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("database", "Connection failed");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/simple")
    public ResponseEntity<List<Map<String, Object>>> simpleProductTest() {
        try {
            List<Product> products = productRepository.findAll(PageRequest.of(0, 5)).getContent();
            List<Map<String, Object>> result = products.stream()
                    .map(p -> {
                        Map<String, Object> productMap = new HashMap<>();
                        productMap.put("id", p.getId());
                        productMap.put("name", p.getName());
                        productMap.put("price", p.getPrice());
                        return productMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
}