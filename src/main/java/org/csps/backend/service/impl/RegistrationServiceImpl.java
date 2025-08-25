package org.csps.backend.service.impl;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.User;
import org.csps.backend.mapper.UserMapper;
import org.csps.backend.service.RegistrationService;
import org.csps.backend.service.StudentService;
import org.csps.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final StudentService studentService;

    public RegistrationServiceImpl(UserService userService, StudentService studentService) {
        this.userService = userService;
        this.studentService = studentService;
    }

    @Transactional
    @Override
    public StudentResponseDTO registerStudent(StudentRequestDTO studentRequestDTO) {
        // create user first using user service
        User savedUser = userService.createUser(studentRequestDTO.getUserRequestDTO());
        // return the created user with student profile
        return studentService.createStudentProfile(studentRequestDTO, savedUser);
    }
}
