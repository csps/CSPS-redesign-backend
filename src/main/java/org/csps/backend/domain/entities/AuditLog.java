package org.csps.backend.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csps.backend.domain.enums.AuditAction;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "audit_log", indexes = {
    @Index(name = "idx_audit_admin_id", columnList = "admin_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* admin who performed the action */
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    /* type of action performed: CREATE, UPDATE, DELETE */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    /* resource type affected (e.g., Merch, Order, Student) */
    @Column(nullable = false)
    private String resourceType;

    /* ID of the affected resource */
    @Column(nullable = false)
    private String resourceId;

    /* description of the change made */
    @Column(columnDefinition = "TEXT")
    private String description;

    /* timestamp when the action was performed */
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
