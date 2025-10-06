package org.csps.backend.domain.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderPostRequestDTO {
    private String studentId;
    
    @NotNull(message = "MerchVariantId is required")
    private Long merchVariantId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;    
}
