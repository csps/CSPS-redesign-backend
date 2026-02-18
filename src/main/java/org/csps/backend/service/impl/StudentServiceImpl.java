package org.csps.backend.service.impl;

import java.util.Optional;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Admin;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.exception.InvalidStudentId;
import org.csps.backend.exception.MissingFieldException;
import org.csps.backend.exception.StudentNotFoundException;
import org.csps.backend.exception.UserAlreadyExistsException;
import org.csps.backend.mapper.StudentMapper;
import org.csps.backend.repository.AdminRepository;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.service.CartService;
import org.csps.backend.service.StudentService;
import org.csps.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

   private final StudentMapper studentMapper;
   private final StudentRepository studentRepository;
   private final AdminRepository adminRepository;
   private final UserService userService;
   private final CartService cartService;
    
    @Override
    @Transactional
    public StudentResponseDTO createStudent(@Valid StudentRequestDTO studentRequestDTO) {

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
        student.setStudentId(studentId);
        
    
        // Persist Student
        student = studentRepository.save(student);
        
        // Create Cart for the student
        cartService.createCart(studentId);
    
        // Map to DTO
        return studentMapper.toResponseDTO(student);

    }


    // Get all Students
   @Override
   public Page<StudentResponseDTO> getAllStudents(Pageable pageable) {
       return studentRepository.findAll(pageable)
               .map(student -> {
                   StudentResponseDTO dto = studentMapper.toResponseDTO(student);
                   enrichStudentWithAdminInfo(dto, student);
                   return dto;
               });
   }


   // Get Student By Id
   @Override
   public StudentResponseDTO getStudentProfile(String studentId) {
       Student existingStudent = studentRepository.findById(studentId)
               .orElseThrow(() -> new StudentNotFoundException(studentId));
       StudentResponseDTO dto = studentMapper.toResponseDTO(existingStudent);
       enrichStudentWithAdminInfo(dto, existingStudent);
       return dto;
   }

   @Override
   public Optional<Student> findByAccountId(Long accountId) {
    return studentRepository.findByUserAccountUserAccountId(accountId);
   }

   @Override
   public Optional<StudentResponseDTO> findById(String id) {
        return studentRepository.findByStudentId(id)
                .map(student -> {
                    StudentResponseDTO dto = studentMapper.toResponseDTO(student);
                    enrichStudentWithAdminInfo(dto, student);
                    return dto;
                });
   }
   
   /* enrich student dto with admin info if they're already an admin */
   private void enrichStudentWithAdminInfo(StudentResponseDTO studentDTO, Student student) {
       if (student.getUserAccount() != null && 
           student.getUserAccount().getUserProfile() != null) {
           
           Long userProfileId = student.getUserAccount().getUserProfile().getUserId();
           
           // check if student is already an admin
           Optional<Admin> adminOpt = adminRepository.findByUserAccount_UserProfile_UserId(userProfileId);

           
           if (adminOpt.isPresent()) {
               Admin admin = adminOpt.get();
               // format admin name as "FIRSTNAME LASTNAME"
            
               studentDTO.setAdminPosition(admin.getPosition());
           }
       }
   }
}
