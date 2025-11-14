package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173/")
@PreAuthorize("hasRole('ADMIN')")
public class StudentController {

   private final StudentService studentService;


   @PostMapping()
   public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody StudentRequestDTO studentRequestDTO) {
       // create student
       StudentResponseDTO createdStudent = studentService.createStudentProfile(studentRequestDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
   }

   @GetMapping()
   public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
       // map all Students to StudentResponseDTO
       List<StudentResponseDTO> students = studentService.getAllStudents();
       return ResponseEntity.ok(students);
   }

   @GetMapping("/{studentId}")
   public ResponseEntity<StudentResponseDTO> getStudent(@PathVariable String studentId) {
       // should be map first to responseDTO
       StudentResponseDTO student = studentService.getStudentProfile(studentId);
       return ResponseEntity.ok(student);
   }
}
