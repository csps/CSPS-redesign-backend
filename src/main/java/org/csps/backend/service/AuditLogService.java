package org.csps.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.entities.AuditLog;
import org.csps.backend.domain.enums.AuditAction;

public interface AuditLogService {

    /* parameters: admin ID, action type, resource type, resource ID, description
       return type: saved AuditLog entity
       logic: creates and persists a new audit log entry to track admin operations */
    AuditLog logAction(Long adminId, AuditAction action, String resourceType, String resourceId, String description);

    /* parameters: admin ID
       return type: list of AuditLog entities
       logic: retrieves all audit logs for a specific admin */
    List<AuditLog> getAuditLogsByAdmin(Long adminId);

    /* parameters: action type
       return type: list of AuditLog entities
       logic: retrieves all audit logs of a specific action type (CREATE, UPDATE, DELETE) */
    List<AuditLog> getAuditLogsByAction(AuditAction action);

    /* parameters: resource type and resource ID
       return type: list of AuditLog entities
       logic: retrieves all audit logs related to a specific resource */
    List<AuditLog> getAuditLogsByResource(String resourceType, String resourceId);

    /* parameters: start time and end time
       return type: list of AuditLog entities
       logic: retrieves all audit logs within a specified time range */
    List<AuditLog> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /* parameters: admin ID, start time, end time
       return type: list of AuditLog entities
       logic: retrieves audit logs for a specific admin within a specified time range */
    List<AuditLog> getAuditLogsByAdminAndTimeRange(Long adminId, LocalDateTime startTime, LocalDateTime endTime);
}
