package org.csps.backend.controller;

import java.util.List;
import java.util.Map;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.service.MerchVariantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/merchVariant")
@RequiredArgsConstructor
public class MerchVariantController {
    private final MerchVariantService merchVariantService;

    @PostMapping("/{merchId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MerchVariantResponseDTO> addVariant(
            @PathVariable Long merchId,
            @RequestBody MerchVariantRequestDTO variantRequest) {
        MerchVariantResponseDTO newVariant = merchVariantService.addVariantToMerch(merchId, variantRequest);
        return ResponseEntity.ok(newVariant);
    }

    @GetMapping("/{merchId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<MerchVariantResponseDTO>> getMerchVariantByMerchId(@PathVariable Long merchId) {
        return ResponseEntity.ok(merchVariantService.getMerchVariantByMerchId(merchId));
    }

    @GetMapping("/{merchId}/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<MerchVariantResponseDTO>> getMerchVariantById(@PathVariable Long merchId) {
        return ResponseEntity.ok(merchVariantService.getMerchVariantByMerchId(merchId));
    }


    @PutMapping("/{merchId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String,Object>> putMerchVariant(@PathVariable Long merchId, @RequestBody MerchVariantUpdateRequestDTO merchVariantUpdateRequestDTO) {
        Map<String,Object> response = merchVariantService.putMerchVariant(merchId, merchVariantUpdateRequestDTO);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{merchId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String,Object>> patchMerchVariant(@PathVariable Long merchId, @RequestBody MerchVariantUpdateRequestDTO merchVariantUpdateRequestDTO) {
        Map<String,Object> response = merchVariantService.patchMerchVariant(merchId, merchVariantUpdateRequestDTO);
        return ResponseEntity.ok(response);
    }
    
}

