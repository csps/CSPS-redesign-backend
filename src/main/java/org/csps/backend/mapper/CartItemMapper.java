package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.CartItemRequestDTO;
import org.csps.backend.domain.dtos.response.CartItemResponseDTO;
import org.csps.backend.domain.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring", uses={MerchVariantMapper.class})
public interface CartItemMapper {

    
    @Mapping(source="id.cartId", target="cartId")
    @Mapping(source="merchVariant.merch.merchName", target="merchName")
    @Mapping(source="quantity", target="quantity")
    @Mapping(source="merchVariant.merch.merchType", target="merchType")
    CartItemResponseDTO toResponseDTO (CartItem cartItem);

    
    @Mapping(target="id", expression="java(new CartItemId(cartItemRequestDTO.getCartId(), cartItemRequestDTO.getMerchVariantId()))")
    @Mapping(source="quantity", target="quantity")
    CartItem toEntity(CartItemRequestDTO cartItemRequestDTO);
}
