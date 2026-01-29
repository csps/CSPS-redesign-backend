package org.csps.backend.domain.dtos.request;

import java.util.List;

import org.csps.backend.domain.enums.ClothingSizing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchVariantItemRequestDTO {
    private ClothingSizing size;
    private Integer stockQuantity;
    private Double price;
}
