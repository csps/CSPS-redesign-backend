package org.csps.backend.controller;

import java.io.IOException;
import java.util.List;

import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.request.MerchUpdateRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.MerchDetailedResponseDTO;
import org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO;
import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.service.MerchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/merch")
@RequiredArgsConstructor
public class MerchController {

    private final MerchService merchService;

    /**
     * Creates a complete merchandise entry including all variants and items.
     * Accepts multipart/form-data for image uploads (main image + variant images).
     */
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<MerchDetailedResponseDTO>> createMerch(
            @RequestParam String merchName,
            @RequestParam String description,
            @RequestParam MerchType merchType,
            @RequestParam(required = true) Double basePrice,
            @RequestParam(required = false) String s3ImageKey,
            @RequestParam(required = true) MultipartFile merchImage,
            @RequestParam String variants,
            @RequestParam(required = true) MultipartFile[] variantImages
    ) throws IOException {
        // Parse variants JSON to list
        ObjectMapper mapper = new ObjectMapper();
        List<MerchVariantRequestDTO> variantsList = mapper.readValue(
            variants, 
            new TypeReference<List<MerchVariantRequestDTO>>() {}
        );


        // Assign variant images to each variant
        if (variantImages != null) {
            for (int i = 0; i < variantsList.size() && i < variantImages.length; i++) {
                variantsList.get(i).setVariantImage(variantImages[i]);
            }
        }


        // Build MerchRequestDTO
        MerchRequestDTO request = MerchRequestDTO.builder()
                .merchName(merchName)
                .description(description)
                .merchType(merchType)
                .basePrice(basePrice)
                .s3ImageKey(s3ImageKey)
                .merchImage(merchImage)
                .merchVariantRequestDto(variantsList)
                .build();


        System.out.println("MERCH REQUEST: " + request);

        MerchDetailedResponseDTO createdMerch = merchService.createMerch(request);
        String message = "Merchandise created successfully with all variants and items";
        return GlobalResponseBuilder.buildResponse(message, createdMerch, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<MerchDetailedResponseDTO>> getAllMerch() {
        return ResponseEntity.ok(merchService.getAllMerch());
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<MerchSummaryResponseDTO>> getAllMerchSummaries() {
        return ResponseEntity.ok(merchService.getAllMerchSummaries());
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<MerchSummaryResponseDTO>> getMerchByType(@PathVariable MerchType type) {
        return ResponseEntity.ok(merchService.getMerchByType(type));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<MerchDetailedResponseDTO> getMerchById(@PathVariable Long id) {
        return ResponseEntity.ok(merchService.getMerchById(id));
    }

    @PutMapping("/{merchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<MerchDetailedResponseDTO>> putMerch(
            @PathVariable Long merchId, 
            @Valid @RequestBody MerchUpdateRequestDTO request
    ) throws IOException {
        MerchDetailedResponseDTO response = merchService.putMerch(merchId, request);
        return GlobalResponseBuilder.buildResponse("Merch Updated Successfully", response, HttpStatus.OK);
    }

    @PatchMapping("/{merchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<MerchDetailedResponseDTO>> patchMerch(
            @PathVariable Long merchId, 
            @RequestBody MerchUpdateRequestDTO request
    ) throws IOException {
        MerchDetailedResponseDTO response = merchService.patchMerch(merchId, request);
        return GlobalResponseBuilder.buildResponse("Merch Updated Successfully", response, HttpStatus.OK);
    }

}