package org.csps.backend.domain.dtos.response;

import java.util.List;

import lombok.Data;

@Data
public class FinanceDashboardDTO {
    private List<InventorySummaryDTO> inventory;
    private List<OrderSummaryDTO> recentOrders;
    private List<StudentMembershipDTO> recentMemberships;
    private MembershipRatioDTO membershipRatio;
    private ChartDataDTO chartData;
}