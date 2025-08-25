package org.csps.backend.service;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;

public interface RegistrationService {
    StudentResponseDTO registerStudent(StudentRequestDTO studentRequestDTO);
}
