package org.csps.backend.domain.dtos.response;

import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.domain.enums.PurchaseItemStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItemResponseDTO {
    private Long purchaseId;

    @JsonProperty("items")
    private MerchVariantResponseDTO merchVariant;
    
    private String merchName;
    private MerchType merchType;
    private int quantity;

    private PurchaseItemStatus status;
}
