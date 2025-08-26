package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByUserAccountUserAccountId(Long userAccountId);
}
