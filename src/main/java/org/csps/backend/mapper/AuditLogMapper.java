package org.csps.backend.mapper;

import java.util.List;

import org.csps.backend.domain.dtos.response.AuditLogResponseDTO;
import org.csps.backend.domain.entities.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    /* maps AuditLog entity to response DTO with admin name from related admin entity */
    @Mapping(source = "admin.adminId", target = "adminId")
    @Mapping(expression = "java(auditLog.getAdmin().getUserAccount().getUserProfile().getFirstName() + \" \" + auditLog.getAdmin().getUserAccount().getUserProfile().getLastName())", target = "adminName")
    AuditLogResponseDTO toResponseDTO(AuditLog auditLog);

    /* maps list of AuditLog entities to list of response DTOs */
    List<AuditLogResponseDTO> toResponseDTOs(List<AuditLog> auditLogs);
}
