package org.csps.backend.service;

import org.csps.backend.domain.entities.EmailVerification;
import org.csps.backend.domain.entities.UserAccount;

public interface EmailVerificationService {
    
    /**
     * send verification code to user's email - initiates the verification process
     */
    EmailVerification sendVerificationCode(Long userAccountId);
    
    /**
     * generate a new 6-digit verification code and send it to user's email
     */
    EmailVerification generateAndSendVerificationCode(UserAccount userAccount);
    
    /**
     * verify the provided code against stored code for user
     */
    EmailVerification verifyCode(Long userAccountId, String code);
    
    /**
     * get active verification for user
     */
    EmailVerification getActiveVerification(Long userAccountId);
    
    /**
     * check if user email is verified
     */
    boolean isUserVerified(Long userAccountId);
    
    /**
     * resend verification code
     */
    EmailVerification resendVerificationCode(Long userAccountId);
    
    /**
     * cleanup expired verifications
     */
    void cleanupExpiredVerifications();
}
