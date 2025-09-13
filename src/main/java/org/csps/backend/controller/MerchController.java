package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.response.MerchResponseDTO;
import org.csps.backend.service.MerchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/merch")
@RequiredArgsConstructor
public class MerchController {

    private final MerchService merchService;

    @PostMapping
    public ResponseEntity<MerchResponseDTO> createMerch(@RequestBody MerchRequestDTO merchRequestDTO) {
        MerchResponseDTO createdMerch = merchService.createMerch(merchRequestDTO);
        return ResponseEntity.ok(createdMerch);
    }

    @GetMapping
    public ResponseEntity<List<MerchResponseDTO>> getAllMerch() {
        List<MerchResponseDTO> merchList = merchService.getAllMerch();
        return ResponseEntity.ok(merchList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MerchResponseDTO> getMerchById(@PathVariable Long id) {
        MerchResponseDTO merch = merchService.getMerchById(id);
        return ResponseEntity.ok(merch);
    }
    
}
