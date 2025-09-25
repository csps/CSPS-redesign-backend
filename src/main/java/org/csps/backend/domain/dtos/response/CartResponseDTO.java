package org.csps.backend.domain.dtos.response;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {
    private String studentId;

    @JsonAlias("cart_items")
    private List<CartItemResponseDTO> cartItemResponseDTOs;
}
