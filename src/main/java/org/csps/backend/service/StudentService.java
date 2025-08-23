package org.csps.backend.service;

import org.csps.backend.domain.dtos.request.StudentPatchDTO;
import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;

import java.util.List;

public interface StudentService {
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO);
    public List<StudentResponseDTO> getAllStudents();
    public StudentResponseDTO getStudent(Long studentId);
    public StudentResponseDTO updateStudent(StudentRequestDTO studentRequestDTO, Long studentId);
    public StudentResponseDTO updateStudent(StudentPatchDTO studentPatchDTO, Long studentId);
    public void deleteStudent(Long studentID);
}
