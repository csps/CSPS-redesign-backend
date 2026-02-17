package org.csps.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.entities.AuditLog;
import org.csps.backend.domain.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /* find all audit logs for a specific admin */
    @Query("SELECT a FROM AuditLog a WHERE a.admin.adminId = :adminId")
    List<AuditLog> findByAdminId(@Param("adminId") Long adminId);

    /* find all audit logs for a specific action type */
    List<AuditLog> findByAction(AuditAction action);

    /* find all audit logs for a specific resource */
    List<AuditLog> findByResourceTypeAndResourceId(String resourceType, String resourceId);

    /* find all audit logs within a time range */
    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startTime AND :endTime ORDER BY a.timestamp DESC")
    List<AuditLog> findByTimestampBetween(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /* find all audit logs for a specific admin within a time range */
    @Query("SELECT a FROM AuditLog a WHERE a.admin.adminId = :adminId AND a.timestamp BETWEEN :startTime AND :endTime ORDER BY a.timestamp DESC")
    List<AuditLog> findByAdminIdAndTimestampBetween(
        @Param("adminId") Long adminId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
