package org.csps.backend.domain.dtos.request;

import org.csps.backend.domain.enums.ClothingSizing;

import lombok.Data;

@Data
public class MerchVariantUpdateRequestDTO {
    private Long merchVariantId;
    private String color;
    private String design;
    private ClothingSizing size;
    private Double price;
    private Integer stockQuantity;
}
