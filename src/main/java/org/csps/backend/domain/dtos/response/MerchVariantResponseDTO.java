package org.csps.backend.domain.dtos.response;

import java.util.List;

import org.csps.backend.domain.enums.ClothingSizing;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private String design;

    private Double price;
    private Integer stockQuantity;

    private String s3ImageKey; 

    @JsonProperty("items")
    private List<MerchVariantItemResponseDTO> variantItems;
}
