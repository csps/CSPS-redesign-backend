package org.csps.backend.service.impl;

import org.csps.backend.domain.dtos.request.CartItemRequestDTO;
import org.csps.backend.domain.dtos.request.RemoveCartItemRequestDTO;
import org.csps.backend.domain.dtos.response.CartItemResponseDTO;
import org.csps.backend.domain.entities.Cart;
import org.csps.backend.domain.entities.CartItem;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.entities.composites.CartItemId;
import org.csps.backend.exception.CartItemNotFoundException;
import org.csps.backend.exception.CartNotFoundException;
import org.csps.backend.exception.MerchVariantNotFoundException;
import org.csps.backend.mapper.CartItemMapper;
import org.csps.backend.repository.CartItemRepository;
import org.csps.backend.repository.CartRepository;
import org.csps.backend.repository.MerchVariantRepository;
import org.csps.backend.service.CartItemService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final MerchVariantRepository merchVariantRepository;
    private final CartRepository cartRepository;

    private final CartItemMapper cartItemMapper;
    
    @Override
    public CartItemResponseDTO addCartItem(CartItemRequestDTO cartItemRequestDTO) {
        String cartId = cartItemRequestDTO.getCartId();
        Long merchVariantId = cartItemRequestDTO.getMerchVariantId();
        int quantity = cartItemRequestDTO.getQuantity();

        MerchVariant merchVariant = merchVariantRepository.findById(merchVariantId)
                                    .orElseThrow(() -> new MerchVariantNotFoundException("Merch Variant Not Found!"));

        // Check if quantity is less than or equal to stock
        if (quantity > merchVariant.getStockQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + quantity + ") exceeds available stock (" + merchVariant.getStockQuantity() + ")");
        }

        Cart cart = cartRepository.findById(cartId)
        .orElseThrow(() -> new CartNotFoundException("Cart not found!"));

        CartItemId cartItemId = new CartItemId(cartId, merchVariantId);

        // Check if item exists
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElse(CartItem.builder()
                        .id(cartItemId)
                        .cart(cart)
                        .merchVariant(merchVariant)
                        .build());

        // Check if total quantity (existing + new) exceeds stock
        int totalQuantity = cartItem.getQuantity() + quantity;
        if (totalQuantity > merchVariant.getStockQuantity()) {
            throw new IllegalArgumentException("Total quantity (" + totalQuantity + ") exceeds available stock (" + merchVariant.getStockQuantity() + ")");
        }

        // Update quantity
        cartItem.setQuantity(totalQuantity);

        // Save
        cartItem = cartItemRepository.save(cartItem);

        // Return DTO
        return cartItemMapper.toResponseDTO(cartItem);
    }

    @Override
    public CartItemResponseDTO removeCartItem(RemoveCartItemRequestDTO removeCartItemRequestDTO) {
        String cartId = removeCartItemRequestDTO.getCartId();
        Long merchVariantId = removeCartItemRequestDTO.getMerchVariantId();

        CartItemId cartItemId = new CartItemId(cartId, merchVariantId);
    
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new CartItemNotFoundException("Cart Item not found"));
            
        CartItemResponseDTO responseDTO = cartItemMapper.toResponseDTO(cartItem);

        // Now we can delete directly using cartItemRepository
        cartItemRepository.deleteById(cartItemId);

        return responseDTO;
    }

}
