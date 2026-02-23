package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.response.AnnouncementResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.service.AnnouncementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcement")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<List<AnnouncementResponseDTO>>> getAllAnnouncements() {
        try {
            List<AnnouncementResponseDTO> announcements = announcementService.getAllAnnouncements();

            String message = "Announcements retrieved successfully";

            return GlobalResponseBuilder.buildResponse(message, announcements, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "Failed to retrieve announcements";
            return GlobalResponseBuilder.buildResponse(errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
