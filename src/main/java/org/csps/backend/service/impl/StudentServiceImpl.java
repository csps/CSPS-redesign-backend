package org.csps.backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.exception.InvalidStudentId;
import org.csps.backend.exception.MissingFieldException;
import org.csps.backend.exception.StudentNotFoundException;
import org.csps.backend.exception.UserAlreadyExistsException;
import org.csps.backend.mapper.StudentMapper;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.service.StudentService;
import org.csps.backend.service.UserService;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

   private final StudentMapper studentMapper;
   private final StudentRepository studentRepository;

   private final UserService userService;
    
    @Override
    public StudentResponseDTO createStudentProfile(@Valid StudentRequestDTO studentRequestDTO) {

        // Check if the student already exists
        String studentId = studentRequestDTO.getStudentId().trim();

        if (studentId.isEmpty()) {
            throw new MissingFieldException("Username cannot be empty!");
        }

        if (studentId.length() != 8) {
            throw new InvalidStudentId("Invalid Student Id");
        }
        
        
        Optional<Student> existingStudent = studentRepository.findByStudentId(studentId);

        // If exists
        if (existingStudent.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User %s already existed", studentId));
        } 

        // If npt
        // Create UserAccount from nested UserRequestDTO
        UserAccount savedUserAccount = userService.createUser(studentRequestDTO, studentRequestDTO.getUserRequestDTO());
    
        // Map Student entity
        Student student = studentMapper.toEntity(studentRequestDTO);
        student.setUserAccount(savedUserAccount);
    
    
        // Persist Student
        student = studentRepository.save(student);
    
        // Map to DTO
        return studentMapper.toResponseDTO(student);

    }


    // Get all Students
   @Override
   public List<StudentResponseDTO> getAllStudents() {
       return studentRepository.findAll().stream()
               .map(studentMapper::toResponseDTO)
               .toList();
   }


   // Get Student By Id
   @Override
   public StudentResponseDTO getStudentProfile(Long studentId) {
       Student existingStudent = studentRepository.findById(studentId)
               .orElseThrow(() -> new StudentNotFoundException(studentId));
       return studentMapper.toResponseDTO(existingStudent);
   }

   @Override
   public Optional<Student> findByAccountId(Long accountId) {
    return studentRepository.findByUserAccountUserAccountId(accountId);
   }

   @Override
   public Optional<Student> findById(String id) {
    return studentRepository.findByStudentId(id);
   }
   
}
