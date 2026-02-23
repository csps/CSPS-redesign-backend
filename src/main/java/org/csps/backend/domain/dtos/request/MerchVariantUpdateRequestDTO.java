package org.csps.backend.domain.dtos.request;

import org.csps.backend.domain.enums.ClothingSizing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchVariantUpdateRequestDTO {
    private String color;
    private String design;
    private ClothingSizing size;
    private Double price;
    private Integer stockQuantity;
}
