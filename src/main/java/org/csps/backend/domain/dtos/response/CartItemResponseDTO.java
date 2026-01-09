package org.csps.backend.domain.dtos.response;

import org.csps.backend.domain.enums.MerchType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDTO {
    private Long cartId;
    private MerchVariantResponseDTO merchVariant;
    private String merchName;
    private MerchType merchType;
    private int quantity;
}
