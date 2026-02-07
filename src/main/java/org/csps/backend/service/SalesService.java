package org.csps.backend.service;

import org.csps.backend.domain.dtos.response.sales.SalesStatsDTO;
import org.csps.backend.domain.dtos.response.sales.TransactionDTO;
import org.csps.backend.domain.enums.SalesPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalesService {
    SalesStatsDTO getSalesStats(SalesPeriod period);
    
    Page<TransactionDTO> getTransactions(Pageable pageable, String search, String status, Integer year);
    
    TransactionDTO approveTransaction(Long id);
    
    void rejectTransaction(Long id);
}