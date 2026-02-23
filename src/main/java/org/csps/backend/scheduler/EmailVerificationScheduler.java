package org.csps.backend.scheduler;

import org.csps.backend.service.EmailVerificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationScheduler {

    private final EmailVerificationService emailVerificationService;

   
    @Scheduled(fixedRate = 1800000) 
    public void cleanupExpiredVerifications() {
        try {
            log.info("starting scheduled cleanup of expired email verifications");
            emailVerificationService.cleanupExpiredVerifications();
            log.info("scheduled cleanup of expired email verifications completed successfully");
        } catch (Exception e) {
            log.error("failed to cleanup expired verifications: {}", e.getMessage(), e);
        }
    }
}
