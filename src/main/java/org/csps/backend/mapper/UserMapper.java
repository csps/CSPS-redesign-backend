package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.CreateUserDTO;
import org.csps.backend.domain.dtos.request.UserPatchDTO;
import org.csps.backend.domain.dtos.request.UserRequestDTO;
import org.csps.backend.domain.dtos.response.UserResponseDTO;
import org.csps.backend.domain.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);
    User toEntity(UserRequestDTO userRequestDTO);

    // update existing user
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserPatchDTO dto, @MappingTarget User user);
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget User user);
}
