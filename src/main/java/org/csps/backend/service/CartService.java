package org.csps.backend.service;

import org.csps.backend.domain.dtos.response.CartResponseDTO;
import org.csps.backend.domain.entities.Cart;

public interface CartService {
    
    /**
     * Get cart for a student by student ID.
     */
    CartResponseDTO getCartByStudentId(String studentId);
    
    /**
     * Create cart for a student.
     */
    Cart createCart(String studentId);
    
    /**
     * Get cart total price.
     */
    Double getCartTotal(String studentId);
    
    /**
     * Get cart item count.
     */
    int getCartItemCount(String studentId);
    
    /**
     * Clear entire cart for student.
     */
    void clearCart(String studentId);
}

