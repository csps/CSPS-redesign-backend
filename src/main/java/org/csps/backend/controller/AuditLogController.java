package org.csps.backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.dtos.response.AuditLogResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.entities.AuditLog;
import org.csps.backend.domain.enums.AuditAction;
import org.csps.backend.mapper.AuditLogMapper;
import org.csps.backend.service.AuditLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final AuditLogMapper auditLogMapper;

    /* retrieve all audit logs for a specific admin */
    @GetMapping("/admin/{adminId}")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<List<AuditLogResponseDTO>>> getAuditLogsByAdmin(
            @PathVariable Long adminId) {

        List<AuditLog> auditLogs = auditLogService.getAuditLogsByAdmin(adminId);
        List<AuditLogResponseDTO> response = auditLogMapper.toResponseDTOs(auditLogs);

        String message = "Retrieved " + response.size() + " audit logs for admin " + adminId;
        return GlobalResponseBuilder.buildResponse(message, response, HttpStatus.OK);
    }

    /* retrieve all audit logs of a specific action type (CREATE, UPDATE, DELETE) */
    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<List<AuditLogResponseDTO>>> getAuditLogsByAction(
            @PathVariable AuditAction action) {

        List<AuditLog> auditLogs = auditLogService.getAuditLogsByAction(action);
        List<AuditLogResponseDTO> response = auditLogMapper.toResponseDTOs(auditLogs);

        String message = "Retrieved " + response.size() + " audit logs for action " + action;
        return GlobalResponseBuilder.buildResponse(message, response, HttpStatus.OK);
    }

    /* retrieve all audit logs related to a specific resource */
    @GetMapping("/resource/{resourceType}/{resourceId}")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<List<AuditLogResponseDTO>>> getAuditLogsByResource(
            @PathVariable String resourceType,
            @PathVariable String resourceId) {

        List<AuditLog> auditLogs = auditLogService.getAuditLogsByResource(resourceType, resourceId);
        List<AuditLogResponseDTO> response = auditLogMapper.toResponseDTOs(auditLogs);

        String message = "Retrieved " + response.size() + " audit logs for " + resourceType + " " + resourceId;
        return GlobalResponseBuilder.buildResponse(message, response, HttpStatus.OK);
    }

    /* retrieve all audit logs within a specified time range */
    @GetMapping("/range")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<List<AuditLogResponseDTO>>> getAuditLogsByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        List<AuditLog> auditLogs = auditLogService.getAuditLogsByTimeRange(startTime, endTime);
        List<AuditLogResponseDTO> response = auditLogMapper.toResponseDTOs(auditLogs);

        String message = "Retrieved " + response.size() + " audit logs between " + startTime + " and " + endTime;
        return GlobalResponseBuilder.buildResponse(message, response, HttpStatus.OK);
    }

    /* retrieve audit logs for a specific admin within a specified time range */
    @GetMapping("/admin/{adminId}/range")
    @PreAuthorize("hasRole('ADMIN_EXECUTIVE')")
    public ResponseEntity<GlobalResponseBuilder<List<AuditLogResponseDTO>>> getAuditLogsByAdminAndTimeRange(
            @PathVariable Long adminId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        List<AuditLog> auditLogs = auditLogService.getAuditLogsByAdminAndTimeRange(adminId, startTime, endTime);
        List<AuditLogResponseDTO> response = auditLogMapper.toResponseDTOs(auditLogs);

        String message = "Retrieved " + response.size() + " audit logs for admin " + adminId + " between " + startTime + " and " + endTime;
        return GlobalResponseBuilder.buildResponse(message, response, HttpStatus.OK);
    }
}
