package org.csps.backend.domain.dtos.response.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChartPointDTO {
    private String label;  // e.g., "Week 1", "Jan", "2024"
    private BigDecimal value;
}