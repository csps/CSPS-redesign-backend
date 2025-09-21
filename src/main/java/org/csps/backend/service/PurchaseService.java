package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.request.PurchaseRequestDTO;
import org.csps.backend.domain.dtos.response.PurchaseResponseDTO;

public interface PurchaseService {
    PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseRequestDTO);
    List<PurchaseResponseDTO> getAllPurchases();
}
