package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.PurchaseRequestDTO;
import org.csps.backend.domain.dtos.response.PurchaseResponseDTO;
import org.csps.backend.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    
    @GetMapping 
    ResponseEntity<List<PurchaseResponseDTO>> getAllPurchase() {
        return ResponseEntity.ok(purchaseService.getAllPurchases());
    }


    @PostMapping("")
    ResponseEntity<PurchaseResponseDTO> createPurchaseResponseDTO(@RequestBody PurchaseRequestDTO purchaseRequestDTO) {
        return ResponseEntity.ok(purchaseService.createPurchase(purchaseRequestDTO));
    }
    

}
