package org.csps.backend.controller;

import org.csps.backend.domain.dtos.request.AdminPostRequestDTO;
import org.csps.backend.domain.dtos.response.AdminResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/add")
    public ResponseEntity<GlobalResponseBuilder<AdminResponseDTO>> addAdmin(@RequestBody AdminPostRequestDTO adminPostRequestDTO) {
    
        AdminResponseDTO adminResponseDTO = adminService.createAdmin(adminPostRequestDTO);

        String message = "Admin added successfully";

        return GlobalResponseBuilder.buildResponse(message, adminResponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{adminId}")
    public ResponseEntity<GlobalResponseBuilder<AdminResponseDTO>> deleteAdmin(@PathVariable Long adminId) {
        AdminResponseDTO adminResponseDTO = adminService.deleteAdmin(adminId);
        String message = "Admin deleted successfully";
        return GlobalResponseBuilder.buildResponse(message, adminResponseDTO, HttpStatus.OK);
    }


}
