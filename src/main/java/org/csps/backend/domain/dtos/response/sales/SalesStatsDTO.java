package org.csps.backend.domain.dtos.response.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesStatsDTO {
    private BigDecimal totalSales;    // e.g., 7666.00
    private String currency;          // e.g., "PHP"
    private List<ChartPointDTO> chartData;
}