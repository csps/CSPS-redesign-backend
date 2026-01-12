package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.request.MerchUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.MerchDetailedResponseDTO;
import org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO;
import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.service.MerchService;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/merch")
@RequiredArgsConstructor
public class MerchController {

    private final MerchService merchService;

    @PostMapping("/post")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MerchDetailedResponseDTO> createMerch(@RequestBody MerchRequestDTO merchRequestDTO) {
        MerchDetailedResponseDTO createdMerch = merchService.createMerch(merchRequestDTO);
        return ResponseEntity.ok(createdMerch);
    }
    

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<MerchDetailedResponseDTO>> getAllMerch() {
        List<MerchDetailedResponseDTO> merchList = merchService.getAllMerch();
        return ResponseEntity.ok(merchList);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<MerchSummaryResponseDTO>> getAllMerchWithoutVariants() {
        List<MerchSummaryResponseDTO> merchList = merchService.getAllMerchWithoutVariants();
        return ResponseEntity.ok(merchList);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<MerchSummaryResponseDTO>> getMerchByType(@PathVariable MerchType type) {
        List<MerchSummaryResponseDTO> merchList = merchService.getMerchByType(type);
        return ResponseEntity.ok(merchList);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<MerchDetailedResponseDTO> getMerchById(@PathVariable Long id) {
        MerchDetailedResponseDTO merch = merchService.getMerchById(id);
        return ResponseEntity.ok(merch);
    }

    @PutMapping("/update/{merchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<MerchDetailedResponseDTO>> putMerch(@PathVariable Long merchId, @RequestBody MerchUpdateRequestDTO merchUpdateRequestDTO) {
        MerchDetailedResponseDTO merchResponseDTO = merchService.putMerch(merchId, merchUpdateRequestDTO);

        String message = "Merch Updated Successfully";
        return GlobalResponseBuilder.buildResponse(message, merchResponseDTO, HttpStatus.OK);
    }

    @PatchMapping("/update/{merchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<MerchDetailedResponseDTO>> patchMerch(@PathVariable Long merchId, @RequestBody MerchUpdateRequestDTO merchUpdateRequestDTO) {
        MerchDetailedResponseDTO merchResponseDTO = merchService.patchMerch(merchId, merchUpdateRequestDTO);

        String message = "Merch Updated Successfully";

        return GlobalResponseBuilder.buildResponse(message, merchResponseDTO, HttpStatus.OK);
    }
    
    
}
