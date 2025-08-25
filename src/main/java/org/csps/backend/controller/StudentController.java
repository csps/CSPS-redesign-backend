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
// TODO create UserController, and get use UserService to get the user, if not exist create the sure
// TODO So, Student and Admin are role based tables
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    StudentController(StudentService studentService) {
            this.studentService = studentService;
    }

//    @PostMapping()
//    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentRequestDTO studentRequestDTO) {
//        StudentResponseDTO createdStudent = studentService.createStudentProfile(studentRequestDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
//    }

    @GetMapping()
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        List<StudentResponseDTO> students = studentService.getAllStudents(); // map all Students to StudentResponseDTO
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> getStudent(@PathVariable Long studentId) {
        StudentResponseDTO student = studentService.getStudentProfile(studentId); // should be map first to responseDTO
        return ResponseEntity.ok(student);
    }

    @PatchMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> patchStudent(@Valid @RequestBody StudentPatchDTO studentPatchDTO) {
        StudentResponseDTO updatedStudent = studentService.patchStudentProfile(studentPatchDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> updateStudent(@Valid @RequestBody StudentRequestDTO studentRequestDTO) {
        StudentResponseDTO updatedStudent = studentService.updateStudentProfile(studentRequestDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    // Test
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudentProfile(studentId);
        return ResponseEntity.noContent().build();
    }
}
