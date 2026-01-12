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
    private Long merchVariantItemId; // Specific SKU ID
    private String merchName;
    private String size;             // e.g., "M" (Null if not clothing)
    private String color;            
    private String design;           
    private String s3ImageKey;       // Variant-specific image
    private Double unitPrice;        // Current price
    private int quantity;
    private Double subTotal;         // unitPrice * quantity
}