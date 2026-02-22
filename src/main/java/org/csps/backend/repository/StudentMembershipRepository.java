package org.csps.backend.repository;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.entities.StudentMembership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentMembershipRepository extends JpaRepository<StudentMembership, Long> {
    List<StudentMembership> findByStudentStudentId(String studentId);
    Optional<StudentMembership> findByStudentStudentIdAndActive(String studentId, boolean isActive);
    Optional<StudentMembership> findByStudentStudentIdAndAcademicYearAndSemester(String studentId, Byte academicYear, Byte semester);
    
    List<StudentMembership> findTop5ByOrderByDateJoinedDesc();
    
    long countByActiveTrue();
    
    Page<StudentMembership> findAll(Pageable pageable);
    
    Page<StudentMembership> findByStudentStudentId(String studentId, Pageable pageable);

    /* check if student has active membership */
    @Query("SELECT CASE WHEN COUNT(sm) > 0 THEN true ELSE false END FROM StudentMembership sm WHERE sm.student.studentId = :studentId AND sm.active = true")
    boolean hasActiveMembership(@Param("studentId") String studentId);
}