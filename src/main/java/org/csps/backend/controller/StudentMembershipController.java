package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.StudentMembershipRequestDTO;
import org.csps.backend.domain.dtos.response.StudentMembershipResponseDTO;
import org.csps.backend.service.StudentMembershipService;
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
@RequestMapping("/api/student-memberships")
@RequiredArgsConstructor
public class StudentMembershipController {

    private final StudentMembershipService studentMembershipService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentMembershipResponseDTO> createStudentMembership(@RequestBody StudentMembershipRequestDTO studentMembershipRequestDTO) {
        StudentMembershipResponseDTO createdMembership = studentMembershipService.createStudentMembership(studentMembershipRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMembership);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentMembershipResponseDTO>> getAllStudentMemberships() {
        List<StudentMembershipResponseDTO> memberships = studentMembershipService.getAllStudentMemberships();
        return ResponseEntity.ok(memberships);
    }

    @GetMapping("/{membershipId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentMembershipResponseDTO> getStudentMembership(@PathVariable Long membershipId) {
        StudentMembershipResponseDTO membership = studentMembershipService.getStudentMembershipById(membershipId)
                                                .orElseThrow(() -> new RuntimeException("Membership not found with ID: " + membershipId));
        return ResponseEntity.ok(membership);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentMembershipResponseDTO>> getStudentWithMemberships(@PathVariable String studentId) {
        List<StudentMembershipResponseDTO> response = studentMembershipService.getStudentWithMemberships(studentId);
        return ResponseEntity.ok(response);
    }
}