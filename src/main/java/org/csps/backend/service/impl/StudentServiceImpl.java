package org.csps.backend.service.impl;

import org.csps.backend.domain.dtos.request.StudentPatchDTO;
import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.User;
import org.csps.backend.exception.StudentAlreadyExistsException;
import org.csps.backend.exception.StudentNotFoundException;
import org.csps.backend.exception.StudentUsernameAlreadyExistsException;
import org.csps.backend.mapper.StudentMapper;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentMapper studentMapper, StudentRepository studentRepository) {
        this.studentMapper = studentMapper;
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentResponseDTO createStudentProfile(StudentRequestDTO studentRequestDTO, User savedUser) {
        Long studentId = Long.valueOf(studentRequestDTO.getStudentId());
        String username = studentRequestDTO.getUsername();
        if (studentRepository.existsById(studentId)) { // checks if the user already have a student profile
            throw new StudentAlreadyExistsException(studentId);
        }
        if (studentRepository.existsByUsername(username)) {
            throw new StudentUsernameAlreadyExistsException(username);
        }
        Student student = studentMapper.toEntity(studentRequestDTO);
        student.setUser(savedUser);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponseDTO(savedStudent);
    }

    @Override
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toResponseDTO)
                .toList();
    }

    @Override
    public StudentResponseDTO getStudentProfile(Long studentId) {
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return studentMapper.toResponseDTO(existingStudent);
    }

    @Override
    public StudentResponseDTO updateStudentProfile(StudentRequestDTO studentRequestDTO) {
        Long studentId = Long.valueOf(studentRequestDTO.getStudentId());
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        studentMapper.updateEntityFromPutDto(studentRequestDTO, existingStudent);
        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toResponseDTO(updatedStudent);
    }

    @Override
    public StudentResponseDTO patchStudentProfile(StudentPatchDTO studentPatchDTO) {
        Long studentId = Long.valueOf(studentPatchDTO.getStudentId());
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        studentMapper.updateEntityFromPatchDto(studentPatchDTO, existingStudent);
        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toResponseDTO(updatedStudent);
    }

    // Just delete the student profile
    @Override
    public void deleteStudentProfile(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
        studentRepository.deleteById(studentId);
    }
}
