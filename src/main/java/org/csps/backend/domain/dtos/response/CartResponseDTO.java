package org.csps.backend.domain.dtos.response;


import java.util.List;

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
    private List<CartItemResponseDTO> cartItemResponseDTOs;
}
