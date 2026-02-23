package org.csps.backend.domain.dtos.response;

import java.util.List;

import lombok.Data;

@Data
public class ChartDataDTO {
    // Array of numbers for the last 7 days
    private List<Integer> weeklyOrders;

    // Optional: If you want to send revenue data as well
    private List<Double> weeklyRevenue;

    // Labels corresponding to the data (e.g., ["Mon", "Tue", ...])
    private List<String> days;
}