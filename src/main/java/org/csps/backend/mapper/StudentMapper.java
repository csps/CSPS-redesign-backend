package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.StudentPatchDTO;
import org.csps.backend.domain.dtos.request.StudentRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class StudentMapper {
    @Autowired
    private UserMapper userMapper;

    // Map Student entity → StudentResponseDTO
    @Mapping(source = "user", target = "userResponseDTO")
    public abstract StudentResponseDTO toResponseDTO(Student student);

    // Map StudentRequestDTO → Student entity
    @Mapping(source = "userRequestDTO", target = "user")
    public abstract Student toEntity(StudentRequestDTO dto);

    // PATCH update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true) // nested update handled in @AfterMapping
    public abstract void updateEntityFromPatchDto(StudentPatchDTO dto, @MappingTarget Student student);

    // PUT update (full replacement)
    @Mapping(target = "user", ignore = true)
    public abstract void updateEntityFromPutDto(StudentRequestDTO dto, @MappingTarget Student student);

    // Handle nested User updates automatically
    @AfterMapping
    protected void handleNestedUserUpdate(StudentPatchDTO dto, @MappingTarget Student student) {
        if (dto.getUserPatchDTO() != null) {
            if (student.getUser() == null) {
                student.setUser(new User());
            }
            userMapper.updateEntityFromDto(dto.getUserPatchDTO(), student.getUser());
        }
    }
}
