package com.ecom.productcatalog.service;

import com.ecom.productcatalog.dto.ProductRequest;
import com.ecom.productcatalog.model.Category;
import com.ecom.productcatalog.model.Product;
import com.ecom.productcatalog.repository.CartItemRepository;
import com.ecom.productcatalog.repository.CategoryRepository;
import com.ecom.productcatalog.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

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
        return productRepository.findWithFilters(categoryId, minPrice, maxPrice, searchTerm, isNew, onSale, pageable);
    }

    // ✅ ADDED: Service method for the admin controller.
    public Page<Product> getAllProductsForAdmin(Pageable pageable) {
        return productRepository.findAllForAdmin(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Product addProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productRequest.getCategoryId()));

        Product product = new Product();
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

        if (productRequest.getCategoryId() != null && !productRequest.getCategoryId().equals(productToUpdate.getCategory().getId())) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productRequest.getCategoryId()));
            productToUpdate.setCategory(category);
        }

        return productRepository.save(productToUpdate);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        cartItemRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }
}