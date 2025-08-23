package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.StudentPatchDTO;
import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UserMapper.class})  // Spring can inject this mapper
public interface StudentMapper {

    // Map Student entity → StudentResponseDTO
    @Mapping(source = "user", target = "userResponseDTO") // map nested User → UserResponseDTO
    StudentResponseDTO toResponseDTO(Student student);

    // Map StudentRequestDTO → Student entity
    @Mapping(source = "userRequestDTO", target = "user") // assign user by id reference
    Student toEntity(StudentRequestDTO dto);

    // PATCH update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true) // we handle user manually
    void updateEntityFromPatchDto(StudentPatchDTO dto, @MappingTarget Student student);

    // PUT update (full replacement)
    @Mapping(target = "user", ignore = true) // handle nested update manually
    void updateEntityFromPutDto(StudentRequestDTO dto, @MappingTarget Student student);
}
