package org.csps.backend.controller;

import jakarta.validation.Valid;
import org.csps.backend.domain.dtos.request.StudentPatchDTO;
import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    StudentController(StudentService studentService) {
            this.studentService = studentService;
    }

    @PostMapping()
    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentRequestDTO studentRequestDTO) {
        StudentResponseDTO createdStudent = studentService.createStudent(studentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @GetMapping()
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        List<StudentResponseDTO> students = studentService.getAllStudents(); // map all Students to StudentResponseDTO
        return ResponseEntity.ok(students);
    }

    // Test
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> getStudent(@PathVariable Long studentId) {
        StudentResponseDTO student = studentService.getStudent(studentId); // should be map first to responseDTO
        return ResponseEntity.ok(student);
    }

    @PatchMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> updateStudent(@Valid @RequestBody StudentPatchDTO studentPatchDTO, @PathVariable Long studentId) {
        StudentResponseDTO updatedStudent = studentService.updateStudent(studentPatchDTO, studentId);
        return ResponseEntity.ok(updatedStudent);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> updateStudent(@Valid @RequestBody StudentRequestDTO studentRequestDTO, @PathVariable Long studentId) {
        StudentResponseDTO updatedStudent = studentService.updateStudent(studentRequestDTO, studentId);
        return ResponseEntity.ok(updatedStudent);
    }

    // Test
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }
}
