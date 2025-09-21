package org.csps.backend.service;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Student;


public interface StudentService {
   StudentResponseDTO createStudentProfile(StudentRequestDTO studentRequestDTO);
   public List<StudentResponseDTO> getAllStudents();
   StudentResponseDTO getStudentProfile(Long studentId);
    Optional<Student> findByAccountId(Long accountId);
    Optional<Student> findById(String id);
}
