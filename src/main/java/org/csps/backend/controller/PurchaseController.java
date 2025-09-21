package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.PurchaseRequestDTO;
import org.csps.backend.domain.dtos.response.PurchaseResponseDTO;
import org.csps.backend.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<PurchaseResponseDTO>> getAllPurchase() {
        // get all purchases
        return ResponseEntity.ok(purchaseService.getAllPurchases());
    }


    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    ResponseEntity<PurchaseResponseDTO> createPurchaseResponseDTO(@AuthenticationPrincipal String studentId, @RequestBody PurchaseRequestDTO purchaseRequestDTO) {
        // get student id from authentication
        purchaseRequestDTO.setStudentId(studentId);
        // create purchase
        return ResponseEntity.ok(purchaseService.createPurchase(purchaseRequestDTO));
    }
    

}
