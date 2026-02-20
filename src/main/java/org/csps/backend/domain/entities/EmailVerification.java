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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_verification", indexes = {
    @Index(name = "idx_user_account_id", columnList = "user_account_id"),
    @Index(name = "idx_verification_code", columnList = "verification_code"),
    @Index(name = "idx_expires_at", columnList = "expires_at"),
    @Index(name = "idx_is_verified", columnList = "is_verified")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailVerificationId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;
    
    @Column(nullable = false, length = 6)
    private String verificationCode;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Default
    private Boolean isVerified = false;
    
    @Column
    private LocalDateTime verifiedAt;

    @Column
    private String newEmail;
    
    @Column(nullable = false)
    private Integer attemptCount = 0;
    
    @Column(nullable = false)
    private Integer maxAttempts = 5;
    
    /**
     * check if verification code is still valid (not expired and not verified and attempts available)
     */
    public boolean isValid() {
        return !isVerified && LocalDateTime.now().isBefore(expiresAt) && attemptCount < maxAttempts;
    }
    
    /**
     * check if max attempts reached
     */
    public boolean isMaxAttemptsReached() {
        return attemptCount >= maxAttempts;
    }
}
