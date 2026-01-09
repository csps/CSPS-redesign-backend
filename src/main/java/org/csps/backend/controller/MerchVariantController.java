package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.ClothingResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.service.MerchVariantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/{merchId}/size/{size}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<MerchVariantResponseDTO> getMerchVariantBySize(
            @PathVariable Long merchId,
            @PathVariable ClothingSizing size) {
        MerchVariantResponseDTO merchVariant = merchVariantService.getMerchVariantBySize(
                size,
                merchId);
        return ResponseEntity.ok(merchVariant);
    }

    @GetMapping("/{merchId}/find")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<MerchVariantResponseDTO> findMerchVariant(
            @PathVariable Long merchId,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) ClothingSizing size,
            @RequestParam(required = false) String design) {

        MerchVariantResponseDTO merchVariant = merchVariantService.getMerchVariant(merchId, color, size, design);
        return ResponseEntity.ok(merchVariant);
    }

    @GetMapping("/{merchId}/available/sizes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<java.util.List<ClothingSizing>> getAvailableSizesForColor(
            @PathVariable Long merchId,
            @RequestParam String color) {
        java.util.List<ClothingSizing> sizes = merchVariantService.getAvailableSizesForColor(merchId, color);
        return ResponseEntity.ok(sizes);
    }

    @GetMapping("/{merchId}/clothing/size")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<org.csps.backend.domain.dtos.response.ClothingResponseDTO> getClothingBySize(
            @PathVariable Long merchId,
            @RequestParam ClothingSizing size) {
        ClothingResponseDTO dto = merchVariantService.getClothingBySize(merchId, size);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{merchId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<MerchVariantResponseDTO>> putMerchVariant(@PathVariable Long merchId, @RequestBody MerchVariantUpdateRequestDTO merchVariantUpdateRequestDTO) {
        MerchVariantResponseDTO response = merchVariantService.putMerchVariant(merchId, merchVariantUpdateRequestDTO);

        String message = "Merch Variant Updated Successfully";

        return GlobalResponseBuilder.buildResponse(message, response, HttpStatus.OK);
    }
    
    @PatchMapping("/{merchId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<MerchVariantResponseDTO>> patchMerchVariant(@PathVariable Long merchId, @RequestBody MerchVariantUpdateRequestDTO merchVariantUpdateRequestDTO) {
        MerchVariantResponseDTO response = merchVariantService.patchMerchVariant(merchId, merchVariantUpdateRequestDTO);

        String message = "Merch Variant Updated Successfully";

        return GlobalResponseBuilder.buildResponse(message, response, HttpStatus.OK);
    }
    
}

