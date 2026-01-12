package org.csps.backend.controller;

import org.csps.backend.domain.dtos.response.CartResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Get cart for authenticated student.
     */
    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<CartResponseDTO>> getCart(
            @AuthenticationPrincipal String studentId) {
        CartResponseDTO cart = cartService.getCartByStudentId(studentId);
        return GlobalResponseBuilder.buildResponse("Cart retrieved successfully", cart, HttpStatus.OK);
    }

    /**
     * Get cart total price.
     */
    @GetMapping("/total")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<Double>> getCartTotal(
            @AuthenticationPrincipal String studentId) {
        Double total = cartService.getCartTotal(studentId);
        return GlobalResponseBuilder.buildResponse("Cart total retrieved successfully", total, HttpStatus.OK);
    }

    /**
     * Get cart item count.
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<Integer>> getCartItemCount(
            @AuthenticationPrincipal String studentId) {
        int count = cartService.getCartItemCount(studentId);
        return GlobalResponseBuilder.buildResponse("Cart item count retrieved successfully", count, HttpStatus.OK);
    }

    /**
     * Clear entire cart.
     */
    @DeleteMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<Void>> clearCart(
            @AuthenticationPrincipal String studentId) {
        cartService.clearCart(studentId);
        return GlobalResponseBuilder.buildResponse("Cart cleared successfully", null, HttpStatus.NO_CONTENT);
    }

    /**
     * Create cart for student (admin only - usually done during student registration).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<CartResponseDTO>> createCart(
            @AuthenticationPrincipal String adminId) {
        // This endpoint is meant to be used by admin to create a cart for a student
        // In typical flow, cart is auto-created during student registration
        throw new UnsupportedOperationException("Cart creation should be handled during student registration");
    }
}
