package org.csps.backend.service.impl;

import org.csps.backend.domain.dtos.response.CartResponseDTO;
import org.csps.backend.domain.entities.Cart;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.exception.CartNotFoundException;
import org.csps.backend.mapper.CartMapper;
import org.csps.backend.repository.CartRepository;
import org.csps.backend.service.CartService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Override
    public CartResponseDTO getCartByStudentId(String studentId) {
        Cart cart = cartRepository.findById(studentId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for student: " + studentId));

        return cartMapper.toResponseDTO(cart);
    }

    @Override
    @Transactional
    public Cart createCart(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        Cart cart = Cart.builder()
                .cartId(studentId)
                .build();
        return cartRepository.save(cart);
    }
}

