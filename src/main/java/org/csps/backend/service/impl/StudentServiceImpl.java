package org.csps.backend.service.impl;

import org.csps.backend.domain.dtos.request.StudentPatchDTO;
import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.User;
import org.csps.backend.exceptions.StudentNotFoundException;
import org.csps.backend.mapper.StudentMapper;
import org.csps.backend.mapper.UserMapper;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.repository.UserRespository;
import org.csps.backend.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final UserRespository userRepository;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper, UserMapper userMapper, UserRespository userRespository) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.userMapper = userMapper;
        this.userRepository = userRespository;
    }

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) {
        User user =  userMapper.toEntity(studentRequestDTO.getUserRequestDTO());
        User savedUser = userRepository.save(user); // persist the user first
        Student student = studentMapper.toEntity(studentRequestDTO);
        student.setUser(savedUser);// add savedUser to student
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponseDTO(savedStudent);
    }

    @Override
    public List<StudentResponseDTO> getAllStudents() { // TODO consider pagination
        return studentRepository.findAll().stream() // get all Students
                .map(studentMapper::toResponseDTO) // map each Student to StudentResponseDTO
                .toList();
    }

    @Override
    public StudentResponseDTO getStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return studentMapper.toResponseDTO(student);
    }

    // PUT update
    @Override
    public StudentResponseDTO updateStudent(StudentRequestDTO studentRequestDTO, Long studentId) {
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        studentMapper.updateEntityFromPutDto(studentRequestDTO, existingStudent);
        if (studentRequestDTO.getUserRequestDTO() != null) {
            userMapper.updateEntityFromDto(studentRequestDTO.getUserRequestDTO(), existingStudent.getUser());
        }
        return studentMapper.toResponseDTO(studentRepository.save(existingStudent));
    }

    // PATCH update
    @Override
    public StudentResponseDTO updateStudent(StudentPatchDTO studentPatchDTO, Long studentId) {
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Update Student fields (ignoring nested User)
        studentMapper.updateEntityFromPatchDto(studentPatchDTO, existingStudent);

        // Update nested User if present
        if (studentPatchDTO.getUserPatchDTO() != null) {
            userMapper.updateEntityFromDto(studentPatchDTO.getUserPatchDTO(), existingStudent.getUser());
        }

        Student savedStudent = studentRepository.save(existingStudent);
        return studentMapper.toResponseDTO(savedStudent);
    }


    @Override
    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
        studentRepository.deleteById(studentId);
    }
}
