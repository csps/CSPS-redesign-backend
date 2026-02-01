package org.csps.backend.controller;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

   private final StudentService studentService;

   @PostMapping()
   @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
   public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody StudentRequestDTO studentRequestDTO) {
       // create student
       StudentResponseDTO createdStudent = studentService.createStudent(studentRequestDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
   }

   @GetMapping()
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Page<StudentResponseDTO>> getAllStudents(
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "7") int size) {
       Pageable pageable = PageRequest.of(page, size);
       Page<StudentResponseDTO> students = studentService.getAllStudents(pageable);
       return ResponseEntity.ok(students);
   }

   @GetMapping("/{studentId}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<StudentResponseDTO> getStudent(@PathVariable String studentId) {
       // should be map first to responseDTO
       StudentResponseDTO student = studentService.getStudentProfile(studentId);
       return ResponseEntity.ok(student);
   }
}
