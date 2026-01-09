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
public class MerchSummaryResponseDTO {
    private Long merchId;
    private String merchName;
    private String description;
    private MerchType merchType;
    private Double price;
}
