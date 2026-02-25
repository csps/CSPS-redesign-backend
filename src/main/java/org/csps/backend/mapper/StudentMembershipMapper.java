package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.StudentMembershipRequestDTO;
import org.csps.backend.domain.dtos.response.StudentMembershipResponseDTO;
import org.csps.backend.domain.entities.StudentMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMembershipMapper {

    /* request DTO → entity */
    @Mapping(target = "membershipId", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "dateJoined", ignore = true)
    @Mapping(target = "active", ignore = true)
    StudentMembership toEntity(StudentMembershipRequestDTO requestDTO);

    /* entity → response DTO */
    @Mapping(target = "studentId", source = "student.studentId")
    StudentMembershipResponseDTO toResponseDTO(StudentMembership membership);
}
