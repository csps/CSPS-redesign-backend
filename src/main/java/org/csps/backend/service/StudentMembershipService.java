package org.csps.backend.service;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.dtos.request.StudentMembershipRequestDTO;
import org.csps.backend.domain.dtos.response.StudentMembershipResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentMembershipService {
    StudentMembershipResponseDTO createStudentMembership(StudentMembershipRequestDTO requestDTO);
    List<StudentMembershipResponseDTO> getAllStudentMemberships();
    List<StudentMembershipResponseDTO> getStudentWithMemberships(String studentId);
    Optional<StudentMembershipResponseDTO> getStudentMembershipById(Long membershipId);
    StudentMembershipResponseDTO updateStudentMembership(Long membershipId, StudentMembershipRequestDTO requestDTO);
    StudentMembershipResponseDTO getActiveMembershipByStudentId(String studentId);
    /**
     * Get all student memberships with pagination.
     * Default page size is 7 items per page.
     * @param pageable pagination details
     * @return paginated list of student memberships
     */
    Page<StudentMembershipResponseDTO> getAllStudentMembershipsPaginated(Pageable pageable);
    
    /**
     * Get student memberships for a specific student with pagination.
     * @param studentId the student ID
     * @param pageable pagination details
     * @return paginated list of memberships for the student
     */
    Page<StudentMembershipResponseDTO> getStudentMembershipsPaginated(String studentId, Pageable pageable);
}