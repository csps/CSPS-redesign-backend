package org.csps.backend.service;

import org.csps.backend.domain.dtos.request.CartItemRequestDTO;
import org.csps.backend.domain.dtos.response.CartItemResponseDTO;
import java.util.List;

public interface CartItemService {
    
    /**
     * Add item to cart.
     */
    CartItemResponseDTO addCartItem(String studentId, CartItemRequestDTO cartItemRequestDTO);
    
    /**
     * Remove item from cart.
     */
    void removeCartItem(String studentId, Long merchVariantItemId);
    
    /**
     * Update cart item quantity.
     */
    CartItemResponseDTO updateCartItemQuantity(String studentId, Long merchVariantItemId, int quantity);
    
    /**
     * Get all items in student's cart.
     */
    List<CartItemResponseDTO> getCartItems(String studentId);
    
    /**
     * Clear entire cart.
     */
    void clearCart(String studentId);
}

