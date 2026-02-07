package org.csps.backend.controller;

import org.csps.backend.domain.dtos.response.FinanceDashboardDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.service.FinanceDashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class FinanceDashboardController {

    private final FinanceDashboardService dashboardService;

    @GetMapping("/finance")
    @PreAuthorize("hasRole('ADMIN_FINANCE') or hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<FinanceDashboardDTO>> getFinanceDashboard() {
        FinanceDashboardDTO data = dashboardService.getFinanceDashboardData();
        return GlobalResponseBuilder.buildResponse("Finance dashboard data retrieved successfully", data, HttpStatus.OK);
    }
}