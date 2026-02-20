package org.csps.backend.service;

import java.io.InputStream;
import java.util.List;

import org.csps.backend.domain.dtos.request.StudentMigrationRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;

/* service for bulk student migration from CSV */
public interface StudentMigrationService {
    
    /* parse CSV and migrate students in bulk */
    List<StudentResponseDTO> migrateStudentsFromCsv(InputStream csvFile);
    
    /* migrate single student from migration DTO */
    StudentResponseDTO migrateSingleStudent(StudentMigrationRequestDTO dto);
}
