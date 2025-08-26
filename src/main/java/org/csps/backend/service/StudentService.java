package org.csps.backend.service;

import java.util.Optional;

import org.csps.backend.domain.entities.Student;

public interface StudentService {
    Optional<Student> findByAccountId(Long accountId);
    Optional<Student> findById(String id);
}
