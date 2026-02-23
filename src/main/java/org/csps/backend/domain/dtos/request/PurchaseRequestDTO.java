package org.csps.backend.domain.dtos.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseRequestDTO {
    private String studentId;

    @JsonAlias("purchase_items")
    private List<PurchaseItemRequestDTO> items;
    private double receivedMoney;
}
