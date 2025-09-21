package com.ecom.productcatalog.service;

import com.ecom.productcatalog.dto.CartItemRequest;
import com.ecom.productcatalog.model.CartItem;
import com.ecom.productcatalog.model.Product;
import com.ecom.productcatalog.model.User;
import com.ecom.productcatalog.repository.CartItemRepository;
import com.ecom.productcatalog.repository.ProductRepository;
import com.ecom.productcatalog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<CartItem> getCart(Long userId) {
        return cartItemRepository.findByUserIdAndIsWishlisted(userId, false);
    }

    // This method remains for compatibility but is no longer the primary way to get wishlist products
    public List<CartItem> getWishlist(Long userId) {
        return cartItemRepository.findByUserIdAndIsWishlisted(userId, true);
    }

    // ✅ ADD THIS NEW, MORE EFFICIENT METHOD
    public List<Product> getWishlistProducts(Long userId) {
        return cartItemRepository.findWishlistedProductsByUserId(userId);
    }

    @Transactional
    public CartItem addToCartOrWishlist(Long userId, CartItemRequest cartItemRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, product.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            if (cartItemRequest.getQuantity() != null) {
                item.setQuantity(cartItemRequest.getQuantity());
            }
            if (cartItemRequest.getIsWishlisted() != null) {
                item.setIsWishlisted(cartItemRequest.getIsWishlisted());
            }
            return cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(cartItemRequest.getQuantity() != null ? cartItemRequest.getQuantity() : 1);
            newItem.setIsWishlisted(cartItemRequest.getIsWishlisted() != null ? cartItemRequest.getIsWishlisted() : false);
            return cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void removeFromCartOrWishlist(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
