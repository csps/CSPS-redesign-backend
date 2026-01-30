package org.csps.backend.repository;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.entities.StudentMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentMembershipRepository extends JpaRepository<StudentMembership, Long> {
    List<StudentMembership> findByStudentStudentId(String studentId);
    Optional<StudentMembership> findByStudentStudentIdAndActive(String studentId, boolean isActive);
    Optional<StudentMembership> findByStudentStudentIdAndAcademicYearAndSemester(String studentId, Byte academicYear, Byte semester);
}