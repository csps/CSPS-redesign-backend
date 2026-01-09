package org.csps.backend.service;

import org.csps.backend.domain.dtos.response.CartResponseDTO;
import org.csps.backend.domain.entities.Cart;

public interface CartService {
    CartResponseDTO getCartByStudentId(String studentId);
    
    Cart createCart(String studentId);
}
