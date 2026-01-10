package org.csps.backend.controller;

import java.io.IOException;
import java.util.List;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantUpdateRequestDTO;
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
import org.springframework.web.multipart.MultipartFile;
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

    @PostMapping("/{merchId}/add-with-image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<MerchVariantResponseDTO>> addVariantWithImage(
            @PathVariable Long merchId,
            @RequestParam("file") MultipartFile imageFile,
            @RequestParam("color") String color,
            @RequestParam("size") ClothingSizing size,
            @RequestParam("price") Double price,
            @RequestParam("stockQuantity") Integer stockQuantity) {
        try {
            MerchVariantRequestDTO request = new MerchVariantRequestDTO();
            request.setMerchId(merchId);
            request.setColor(color);
            request.setSize(size);
            request.setPrice(price);
            request.setStockQuantity(stockQuantity);
            
            MerchVariantResponseDTO response = merchVariantService.addMerchVariantWithImage(request, imageFile);
            return GlobalResponseBuilder.buildResponse("Variant created with image successfully", response, HttpStatus.CREATED);
        } catch (IOException e) {
            return GlobalResponseBuilder.buildResponse("Failed to upload variant image", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    
    @PostMapping("/{merchVariantId}/upload-image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<String>> uploadVariantImage(
            @PathVariable Long merchVariantId,
            @RequestParam("file") MultipartFile file) {
        try {
            String s3ImageKey = merchVariantService.uploadVariantImage(merchVariantId, file);
            return GlobalResponseBuilder.buildResponse("Image uploaded successfully", s3ImageKey, HttpStatus.OK);
        } catch (IOException e) {
            return GlobalResponseBuilder.buildResponse("Failed to upload image", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}