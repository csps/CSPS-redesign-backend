package org.csps.backend.controller;

import org.csps.backend.domain.entities.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    @PostMapping()
    public ResponseEntity<String> createStudent(Student student) {

        return ResponseEntity.status(HttpStatus.CREATED).body("created");
    }

    @GetMapping()
    public ResponseEntity<List<String>> getStudents() {
        return ResponseEntity.ok(List.of("Ducay", "Haggai", "Pincay"));
    }


}
