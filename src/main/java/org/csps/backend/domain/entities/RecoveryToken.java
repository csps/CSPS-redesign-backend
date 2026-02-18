package org.csps.backend.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recovery_token", indexes = {
    @Index(name = "idx_token", columnList = "token"),
    @Index(name = "idx_user_account_id", columnList = "user_account_id"),
    @Index(name = "idx_expires_at", columnList = "expires_at"),
    @Index(name = "idx_is_used", columnList = "is_used"),
    @Index(name = "idx_user_expires", columnList = "user_account_id, expires_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recoveryTokenId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;
    
    @Column(nullable = false, unique = true, length = 500)
    private String token;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Default
    private Boolean isUsed = false;
    
    @Column
    private LocalDateTime usedAt;
    
    /**
     * check if token is still valid (not expired and not used)
     */
    public boolean isValid() {
        return !isUsed && LocalDateTime.now().isBefore(expiresAt);
    }
}
