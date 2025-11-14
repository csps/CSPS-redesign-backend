package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.AdminResponseDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Admin;
import org.csps.backend.domain.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AdminMapper {

    // Student → DTO
    @Mapping(source = "userAccount", target = "userResponseDTO")
    AdminResponseDTO toResponseDTO(Admin admin);

//    // DTO → Student
//    @Mapping(source = "userRequestDTO", target = "userAccount")
//    Student toEntity(StudentRequestDTO studentRequestDTO);

}
