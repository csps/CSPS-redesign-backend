package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.CartItemRequestDTO;
import org.csps.backend.domain.dtos.response.CartItemResponseDTO;
import org.csps.backend.domain.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(source = "merchVariantItem.merchVariantItemId", target = "merchVariantItemId")
    @Mapping(source = "merchVariantItem.merchVariant.merch.merchName", target = "merchName")
    @Mapping(source = "merchVariantItem.merchVariant.color", target = "color")
    @Mapping(source = "merchVariantItem.merchVariant.design", target = "design")
    @Mapping(source = "merchVariantItem.size", target = "size")
    @Mapping(source = "merchVariantItem.price", target = "unitPrice")
    @Mapping(source = "merchVariantItem.merchVariant.s3ImageKey", target = "s3ImageKey")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(target = "subTotal", expression = "java(cartItem.getQuantity() * (cartItem.getMerchVariantItem() != null ? cartItem.getMerchVariantItem().getPrice() : 0.0))")
    CartItemResponseDTO toResponseDTO(CartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "merchVariantItem", ignore = true)
    @Mapping(source = "quantity", target = "quantity")
    CartItem toEntity(CartItemRequestDTO cartItemRequestDTO);
}
