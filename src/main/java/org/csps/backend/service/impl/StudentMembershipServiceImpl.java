package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.dtos.request.StudentMembershipRequestDTO;
import org.csps.backend.domain.dtos.response.StudentMembershipResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.StudentMembership;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.MemberNotFoundException;
import org.csps.backend.exception.StudentNotFoundException;
import org.csps.backend.mapper.StudentMembershipMapper;
import org.csps.backend.repository.StudentMembershipRepository;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.service.StudentMembershipService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Service implementation for managing student memberships.
 * Provides CRUD operations for StudentMembership entities.
 */
@Service
@RequiredArgsConstructor
public class StudentMembershipServiceImpl implements StudentMembershipService {

    private final StudentMembershipMapper studentMembershipMapper;
    private final StudentMembershipRepository studentMembershipRepository;
    private final StudentRepository studentRepository;

    /**
     * Creates a new student membership.
     *
     * @param requestDTO the request data for creating the membership
     * @return the created membership response DTO
     */
    @Override
    @Transactional
    public StudentMembershipResponseDTO createStudentMembership(@Valid StudentMembershipRequestDTO requestDTO) {
        // Validate student exists
        String studentId = requestDTO.getStudentId();
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));

        // Validate academic year and semester
        if (requestDTO.getAcademicYear() == null) {
            throw new InvalidRequestException("Academic year cannot be null");
        }
        if (requestDTO.getSemester() == null) {
            throw new InvalidRequestException("Semester cannot be null");
        }

        // Validate academic year matches student's year level
        if (!requestDTO.getAcademicYear().equals(student.getYearLevel())) {
            throw new InvalidRequestException("Requested academic year " + requestDTO.getAcademicYear() + " does not match student's year level " + student.getYearLevel());
        }

        // Check if membership already exists for this academic year and semester
        Optional<StudentMembership> existingMembership = studentMembershipRepository
                .findByStudentStudentIdAndAcademicYearAndSemester(studentId, requestDTO.getAcademicYear(), requestDTO.getSemester());
        if (existingMembership.isPresent()) {
            throw new InvalidRequestException("Membership already exists for academic year " + requestDTO.getAcademicYear() + " and semester " + requestDTO.getSemester());
        }

        // Map to entity
        StudentMembership membership = studentMembershipMapper.toEntity(requestDTO);
        membership.setStudent(student);

        // Set academic year and semester from request
        membership.setAcademicYear(requestDTO.getAcademicYear());
        membership.setSemester(requestDTO.getSemester());
        membership.setActive(requestDTO.isActive());

        // Save
        StudentMembership saved = studentMembershipRepository.save(membership);

        // Return DTO
        return studentMembershipMapper.toResponseDTO(saved);
    }

    /**
     * Retrieves all student memberships.
     *
     * @return list of all membership response DTOs
     */
    @Override
    public List<StudentMembershipResponseDTO> getAllStudentMemberships() {
        return studentMembershipRepository.findAll().stream()
                .map(studentMembershipMapper::toResponseDTO)
                .toList();
    }

    /**
     * Retrieves memberships for a specific student.
     *
     * @param studentId the student ID
     * @return list of membership response DTOs
     */
    @Override
    public List<StudentMembershipResponseDTO> getStudentWithMemberships(String studentId) {
        // Get student
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));

        // Get memberships
        List<StudentMembership> memberships = studentMembershipRepository.findByStudentStudentId(studentId);

        // Map to DTO
        return memberships.stream()
                .map(studentMembershipMapper::toResponseDTO)
                .toList();
    }

    /**
     * Retrieves a student membership by ID.
     *
     * @param membershipId the membership ID
     * @return optional of membership response DTO
     */
    @Override
    public Optional<StudentMembershipResponseDTO> getStudentMembershipById(Long membershipId) {
        return studentMembershipRepository.findById(membershipId)
                .map(studentMembershipMapper::toResponseDTO);
    }

    /**
     * Updates an existing student membership.
     *
     * @param membershipId the membership ID to update
     * @param requestDTO the updated data
     * @return the updated membership response DTO
     */
    @Override
    @Transactional
    public StudentMembershipResponseDTO updateStudentMembership(Long membershipId, @Valid StudentMembershipRequestDTO requestDTO) {
        StudentMembership existing = studentMembershipRepository.findById(membershipId)
                .orElseThrow(() -> new MemberNotFoundException("Membership not found with ID: " + membershipId));

        // Update fields
        existing.setActive(requestDTO.isActive());

        // If student changed, but probably not
        if (!existing.getStudent().getStudentId().equals(requestDTO.getStudentId())) {
            String newStudentId = requestDTO.getStudentId();
            Student newStudent = studentRepository.findByStudentId(newStudentId)
                    .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + newStudentId));
            existing.setStudent(newStudent);
        }

        StudentMembership saved = studentMembershipRepository.save(existing);
        return studentMembershipMapper.toResponseDTO(saved);
    }


    /**
     * Retrieves the active membership for a student.
     *
     * @param studentId the student ID
     * @return the active membership response DTO, or null if not found
     */
    @Override
    public StudentMembershipResponseDTO getActiveMembershipByStudentId(String studentId) {
        return studentMembershipRepository.findByStudentStudentIdAndActive(studentId, true)
                .map(studentMembershipMapper::toResponseDTO)
                .orElse(null);
    }}