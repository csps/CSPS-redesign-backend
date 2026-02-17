package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.entities.Admin;
import org.csps.backend.domain.entities.AuditLog;
import org.csps.backend.domain.enums.AuditAction;
import org.csps.backend.repository.AdminRepository;
import org.csps.backend.repository.AuditLogRepository;
import org.csps.backend.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AdminRepository adminRepository;

    @Override
    public AuditLog logAction(Long adminId, AuditAction action, String resourceType, String resourceId, String description) {
        /* fetch admin entity and create audit log entry with current timestamp */
        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));

        AuditLog auditLog = AuditLog.builder()
            .admin(admin)
            .action(action)
            .resourceType(resourceType)
            .resourceId(resourceId)
            .description(description)
            .timestamp(LocalDateTime.now())
            .build();

        return auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByAdmin(Long adminId) {
        return auditLogRepository.findByAdminId(adminId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByAction(AuditAction action) {
        return auditLogRepository.findByAction(action);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByResource(String resourceType, String resourceId) {
        return auditLogRepository.findByResourceTypeAndResourceId(resourceType, resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.findByTimestampBetween(startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByAdminAndTimeRange(Long adminId, LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.findByAdminIdAndTimestampBetween(adminId, startTime, endTime);
    }
}
