package org.csps.backend.controller;

import java.io.IOException;
import java.util.List;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantUpdateRequestDTO;

import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.exception.InvalidRequestException;
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
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/merch-variant")
@RequiredArgsConstructor
public class MerchVariantController {

    private final MerchVariantService merchVariantService;

    /**
     * Adds a new visual variant (Color/Design) to a specific merchandise.
     */
    @PostMapping("/{merchId}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MerchVariantResponseDTO> addVariant(
            @PathVariable Long merchId,
            @RequestBody MerchVariantRequestDTO variantRequest) throws IOException {
        MerchVariantResponseDTO newVariant = merchVariantService.addVariantToMerch(merchId, variantRequest);
        return ResponseEntity.ok(newVariant);
    }

    /**
     * Retrieves all visual variants for a specific merchandise (e.g., all colors available).
     */
    @GetMapping("/merch/{merchId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<MerchVariantResponseDTO>> getVariantsByMerchId(@PathVariable Long merchId) {
        return ResponseEntity.ok(merchVariantService.getVariantsByMerchId(merchId));
    }

    /**
     * Finds a specific variant by its unique key (Color for clothing, Design for others).
     */
    @GetMapping("/{merchId}/find")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<MerchVariantResponseDTO> findVariantByKey(
            @PathVariable Long merchId,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String design) {
        return ResponseEntity.ok(merchVariantService.getVariantByMerchAndKey(merchId, color, design));
    }

    /**
     * Uploads or updates the image for a specific variant.
     */
    @PostMapping("/{merchVariantId}/upload-image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<String>> uploadVariantImage(
            @PathVariable Long merchVariantId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String s3ImageKey = merchVariantService.uploadVariantImage(merchVariantId, file);
        return GlobalResponseBuilder.buildResponse("Image uploaded successfully", s3ImageKey, HttpStatus.OK);
    }

    /**
     * Retrieves all variants across all merchandise (Admin utility).
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MerchVariantResponseDTO>> getAllVariants() {
        return ResponseEntity.ok(merchVariantService.getAllMerchVariants());
    }

 
}