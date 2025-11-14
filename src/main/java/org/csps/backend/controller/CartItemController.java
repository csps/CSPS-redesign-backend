package org.csps.backend.controller;

import org.csps.backend.domain.dtos.request.CartItemRequestDTO;
import org.csps.backend.domain.dtos.request.RemoveCartItemRequestDTO;
import org.csps.backend.domain.dtos.response.CartItemResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<CartItemResponseDTO>> addCartItem(@AuthenticationPrincipal String studentId, @RequestBody CartItemRequestDTO requestDTO) {
        // get student id from authentication
        requestDTO.setCartId(studentId);

        CartItemResponseDTO responseDTO = cartItemService.addCartItem(requestDTO);
        return GlobalResponseBuilder.buildResponse("Cart item added successfully", responseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<CartItemResponseDTO>> removeCartItem(@AuthenticationPrincipal String studentId, @RequestBody RemoveCartItemRequestDTO requestDTO) {
        // get student id from authentication
        requestDTO.setCartId(studentId);

        CartItemResponseDTO responseDTO = cartItemService.removeCartItem(requestDTO);
        return GlobalResponseBuilder.buildResponse("Cart item removed successfully", responseDTO, HttpStatus.OK);
    }

}
