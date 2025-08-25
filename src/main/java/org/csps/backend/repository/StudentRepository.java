package org.csps.backend.repository;

import org.csps.backend.domain.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByUsername(String username);
}
