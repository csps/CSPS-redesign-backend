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
public class MerchVariantItemResponseDTO {
    private Long merchVariantItemId;
    private ClothingSizing size;
    private Integer stockQuantity;
    private Double price;
}
