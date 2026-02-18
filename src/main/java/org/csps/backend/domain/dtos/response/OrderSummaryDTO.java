package org.csps.backend.domain.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderSummaryDTO {
    private Long orderItemId;
    private Long orderId;
    private String studentName;
    private String referenceNumber; // e.g., "ORD-001"
    private String productName;
    private String s3ImageKey;
    private String status; // "PENDING", "PROCESSING", etc.
    private BigDecimal price; // For potential revenue calc
    private LocalDateTime createdAt;
}