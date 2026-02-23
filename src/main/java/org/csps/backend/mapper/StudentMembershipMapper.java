package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.StudentMembershipRequestDTO;
import org.csps.backend.domain.dtos.response.StudentMembershipResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.StudentMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMembershipMapper {

    // Request DTO → Entity
    @Mapping(target = "membershipId", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "dateJoined", ignore = true)
    @Mapping(target = "active", source = "active")
    StudentMembership toEntity(StudentMembershipRequestDTO requestDTO);

    // Entity → Response DTO
    @Mapping(target = "studentId", source = "student.studentId")
    @Mapping(source = "active", target = "active")
    StudentMembershipResponseDTO toResponseDTO(StudentMembership membership);
}
