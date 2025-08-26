package org.csps.backend.service.impl;

import java.util.Optional;

import org.csps.backend.domain.entities.Student;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.service.StudentService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public Optional<Student> findByAccountId(Long accountId) {
        return studentRepository.findByUserAccountUserAccountId(accountId);
    }

    @Override
    public Optional<Student> findById(String id) {
        return studentRepository.findById(id);
    }
}
