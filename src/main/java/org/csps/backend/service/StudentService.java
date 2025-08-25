package org.csps.backend.service;

import org.csps.backend.domain.dtos.request.StudentPatchDTO;
import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.User;

import java.util.List;

public interface StudentService {
    StudentResponseDTO createStudentProfile(StudentRequestDTO studentRequestDTO, User savedUser);
    public List<StudentResponseDTO> getAllStudents();
    StudentResponseDTO getStudentProfile(Long studentId);
    StudentResponseDTO updateStudentProfile(StudentRequestDTO studentRequestDTO);
    StudentResponseDTO patchStudentProfile(StudentPatchDTO studentPatchDTO);
    void deleteStudentProfile(Long studentId);
}
