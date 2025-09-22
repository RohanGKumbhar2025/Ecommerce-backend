package com.ecom.productcatalog.service;

import com.ecom.productcatalog.dto.ProductRequest;
import com.ecom.productcatalog.model.Category;
import com.ecom.productcatalog.model.Product;
import com.ecom.productcatalog.repository.CartItemRepository;
import com.ecom.productcatalog.repository.CategoryRepository;
import com.ecom.productcatalog.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Page<Product> getAllProducts(Long categoryId, Double minPrice, Double maxPrice, String searchTerm, Boolean isNew, Boolean onSale, Pageable pageable) {
        // ✅ FIX: The call to the repository method now correctly omits the 'searchTerm' argument.
        return productRepository.findWithFilters(categoryId, minPrice, maxPrice, isNew, onSale, pageable);
    }

    public Page<Product> getAllProductsSimple(Pageable pageable) {
        logger.info("Using simple product query with pageable: {}", pageable);
        return productRepository.findAll(pageable);
    }

    public Page<Product> getAllProductsForAdmin(Pageable pageable) {
        return productRepository.findAllForAdmin(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product addProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + productRequest.getCategoryId()));

        Product product = new Product();
        // ... (rest of the method is unchanged)
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice().doubleValue());
        product.setImageUrl(productRequest.getImageUrl());
        product.setCategory(category);
        product.setInStock(productRequest.getInStock());
        product.setOriginalPrice(productRequest.getOriginalPrice() != null ? productRequest.getOriginalPrice().doubleValue() : productRequest.getPrice().doubleValue());
        product.setRating(productRequest.getRating() != null ? productRequest.getRating() : 0.0);
        product.setReviews(productRequest.getReviews() != null ? productRequest.getReviews() : 0);
        product.setIsNew(productRequest.getIsNew());
        product.setOnSale(productRequest.getOnSale());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequest productRequest) {
        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        // ... (rest of the method is unchanged)
        Optional.ofNullable(productRequest.getName()).ifPresent(productToUpdate::setName);
        Optional.ofNullable(productRequest.getDescription()).ifPresent(productToUpdate::setDescription);
        Optional.ofNullable(productRequest.getPrice()).ifPresent(price -> productToUpdate.setPrice(price.doubleValue()));
        Optional.ofNullable(productRequest.getImageUrl()).ifPresent(productToUpdate::setImageUrl);
        Optional.ofNullable(productRequest.getInStock()).ifPresent(productToUpdate::setInStock);
        Optional.ofNullable(productRequest.getOriginalPrice()).ifPresent(price -> productToUpdate.setOriginalPrice(price.doubleValue()));
        Optional.ofNullable(productRequest.getRating()).ifPresent(productToUpdate::setRating);
        Optional.ofNullable(productRequest.getReviews()).ifPresent(productToUpdate::setReviews);
        Optional.ofNullable(productRequest.getIsNew()).ifPresent(productToUpdate::setIsNew);
        Optional.ofNullable(productRequest.getOnSale()).ifPresent(productToUpdate::setOnSale);

        if (productRequest.getCategoryId() != null && !productToUpdate.getCategory().getId().equals(productRequest.getCategoryId())) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
            productToUpdate.setCategory(category);
        }

        return productRepository.save(productToUpdate);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        cartItemRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }
}