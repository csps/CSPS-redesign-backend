package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173/")

public class StudentController {

   private final StudentService studentService;

   @PostMapping()
   @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
   public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody StudentRequestDTO studentRequestDTO) {
       // create student
       StudentResponseDTO createdStudent = studentService.createStudentProfile(studentRequestDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
   }

   @GetMapping()
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
       // map all Students to StudentResponseDTO
       List<StudentResponseDTO> students = studentService.getAllStudents();
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
