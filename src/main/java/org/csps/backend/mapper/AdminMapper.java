package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.AdminPostRequestDTO;
import org.csps.backend.domain.dtos.response.AdminResponseDTO;
import org.csps.backend.domain.entities.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AdminMapper {

    // Student → DTO
    @Mapping(source = "userAccount", target = "user")
    AdminResponseDTO toResponseDTO(Admin admin);

   // DTO → Admin
   @Mapping(source = "position", target = "position")
    Admin toEntity(AdminPostRequestDTO adminPostRequestDTO);

}
