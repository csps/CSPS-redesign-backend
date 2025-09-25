package org.csps.backend.controller;

import org.csps.backend.domain.dtos.response.CartResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Get cart by student ID
    @GetMapping("")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<CartResponseDTO>> getCart(@AuthenticationPrincipal String studentId) {
        CartResponseDTO cart = cartService.getCartByStudentId(studentId);
        
        return GlobalResponseBuilder.buildResponse("Cart retrieved successfully", cart, HttpStatus.OK);
    }
}
