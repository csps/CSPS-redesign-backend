package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.response.CartResponseDTO;
import org.csps.backend.domain.entities.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
    @Mapping(target = "studentId", source = "student.studentId")
    @Mapping(target = "cartItemResponseDTOs", source = "items")
    CartResponseDTO toResponseDTO(Cart cart);
}
