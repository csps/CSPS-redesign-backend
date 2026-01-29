package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface StudentMapper {

    // Student → DTO
    @Mapping(source = "userAccount", target = "user")
    StudentResponseDTO toResponseDTO(Student student);

    // DTO → Student
    @Mapping(source = "studentId", target = "studentId")
    @Mapping(source = "yearLevel", target = "yearLevel")
    @Mapping(source = "userRequestDTO", target = "userAccount")
    Student toEntity(StudentRequestDTO studentRequestDTO);

}
