package org.csps.backend.domain.dtos.response;

import lombok.Data;

@Data
public class InventorySummaryDTO {
    private Long id;
    private String name;
    private Integer stock;
    private String s3ImageKey;
    private String stockStatus; // "IN_STOCK", "LOW_STOCK", "OUT_OF_STOCK"
}