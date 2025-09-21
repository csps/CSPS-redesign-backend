package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByUserAccountUserAccountId(Long userAccountId);
}
