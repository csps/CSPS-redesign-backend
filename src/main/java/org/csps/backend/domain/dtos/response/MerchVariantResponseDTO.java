package org.csps.backend.domain.dtos.response;

import org.csps.backend.domain.enums.ClothingSizing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchVariantResponseDTO {
    private Long merchVariantId;
    private String color;
    private ClothingSizing size;
    private Double price;
    private Integer stockQuantity;

    private Long merchId; // reference to MerchResponseDto (to avoid nesting)
    
}
