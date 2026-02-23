package org.csps.backend.domain.dtos.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailVerificationResponseDTO {
    
    private Long emailVerificationId;
    
    private Long userAccountId;
    
    private LocalDateTime createdAt;
    
    @JsonAlias("expiresAt")
    private LocalDateTime expiresAt;
    
    @JsonAlias("isVerified")
    private Boolean isVerified;
    
    private LocalDateTime verifiedAt;
    
    private Integer attemptCount;
    
    private Integer maxAttempts;
    
    /**
     * calculate remaining attempts
     */
    public Integer getRemainingAttempts() {
        return maxAttempts - attemptCount;
    }
}
