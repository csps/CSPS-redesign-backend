package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.service.StudentMigrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/migration")
@RequiredArgsConstructor
public class StudentMigrationController {

    private final StudentMigrationService studentMigrationService;

    /**
     * bulk migrate students from CSV file
     * CSV format: YEAR_LEVEL,LASTNAME,FIRSTNAME,ID_NUMBER
     * creates student accounts with incomplete profiles (isProfileComplete=false)
     * frontend will detect incomplete profiles and redirect to completion form
     *
     * @param file uploaded CSV file
     * @return list of migrated students with isProfileComplete=false
     */
    @PostMapping("/students/csv")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<List<StudentResponseDTO>>> migrateStudentsFromCsv(
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return GlobalResponseBuilder.buildResponse("CSV file is empty", null, HttpStatus.BAD_REQUEST);
            }

            List<StudentResponseDTO> migratedStudents = studentMigrationService
                    .migrateStudentsFromCsv(file.getInputStream());

            String message = String.format("Successfully migrated %d students. They will need to complete their profiles.",
                    migratedStudents.size());

            return GlobalResponseBuilder.buildResponse(message, migratedStudents, HttpStatus.CREATED);

        } catch (Exception e) {
            return GlobalResponseBuilder.buildResponse("CSV migration failed: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }
}
