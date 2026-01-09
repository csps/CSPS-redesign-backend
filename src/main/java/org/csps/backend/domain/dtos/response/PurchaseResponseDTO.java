package org.csps.backend.domain.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponseDTO {
    private Long purchaseId;
    private String studentId;

    @JsonAlias("purchase_items")
    private List<PurchaseItemResponseDTO> items;
    private LocalDateTime purchasedAt;
    
    private Double totalPrice;
    private Double receivedMoney;


}
