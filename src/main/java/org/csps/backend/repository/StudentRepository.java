package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String>, JpaSpecificationExecutor<Student> {
    
    /* eagerly load userAccount and userProfile to avoid N+1 queries */
    @EntityGraph(attributePaths = {"userAccount", "userAccount.userProfile"})
    Page<Student> findAll(Pageable pageable);
    
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.userAccount ua LEFT JOIN FETCH ua.userProfile WHERE s.studentId = :studentId")
    Optional<Student> findByStudentId(@Param("studentId") String studentId);
    
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.userAccount ua LEFT JOIN FETCH ua.userProfile WHERE ua.userAccountId = :accountId")
    Optional<Student> findByUserAccountUserAccountId(@Param("accountId") Long accountId);

    /* efficient query to get student ID for JWT generation without full entity fetch */
    @Query("SELECT s.studentId FROM Student s WHERE s.userAccount.userAccountId = :accountId")
    Optional<String> findStudentIdByUserAccountId(@Param("accountId") Long accountId);
}
