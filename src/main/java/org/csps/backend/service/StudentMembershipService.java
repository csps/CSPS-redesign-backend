package org.csps.backend.service;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.dtos.request.StudentMembershipRequestDTO;
import org.csps.backend.domain.dtos.response.StudentMembershipResponseDTO;
import org.csps.backend.domain.dtos.response.StudentWithMembershipsResponseDTO;

public interface StudentMembershipService {
    StudentMembershipResponseDTO createStudentMembership(StudentMembershipRequestDTO requestDTO);
    List<StudentMembershipResponseDTO> getAllStudentMemberships();
    List<StudentMembershipResponseDTO> getStudentWithMemberships(String studentId);
    Optional<StudentMembershipResponseDTO> getStudentMembershipById(Long membershipId);
    StudentMembershipResponseDTO updateStudentMembership(Long membershipId, StudentMembershipRequestDTO requestDTO);
    StudentMembershipResponseDTO getActiveMembershipByStudentId(String studentId);
}