package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.Random;

import org.csps.backend.domain.entities.EmailVerification;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.exception.EmailAlreadyExistsException;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.ResourceNotFoundException;
import org.csps.backend.repository.EmailVerificationRepository;
import org.csps.backend.repository.UserAccountRepository;
import org.csps.backend.repository.UserProfileRepository;
import org.csps.backend.service.EmailService;
import org.csps.backend.service.EmailVerificationService;
import org.csps.backend.service.UserAccountService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {
    
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository; // Injected
    private final EmailService emailService;
    private final UserAccountService userAccountService; // Injected
    
    private long verificationExpirationMinutes = 5; // default to 10 minutes, can be overridden by application properties
    
    @Override
    @Transactional
    public EmailVerification sendVerificationCode(Long userAccountId) {
        if (userAccountId == null || userAccountId <= 0) {
            throw new InvalidRequestException("user account id is required");
        }
        
        UserAccount userAccount = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("user account not found"));
        
        return generateAndSendVerificationCode(userAccount);
    }
    
    @Override
    @Transactional
    public EmailVerification generateAndSendVerificationCode(UserAccount userAccount) {
        if (userAccount == null || userAccount.getUserAccountId() == null) {
            throw new InvalidRequestException("user account is required");
        }

        if (userAccount.getIsVerified())
            throw new InvalidRequestException("user account is already verified");
        
        // revoke previous verification
        emailVerificationRepository.findByUserAccountUserAccountIdAndNewEmailIsNull(userAccount.getUserAccountId())
                .ifPresent(ev -> {
                    if (!ev.getIsVerified()) {
                        emailVerificationRepository.delete(ev);
                    }
                });

        
        
        // generate 6-digit code
        String verificationCode = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(verificationExpirationMinutes);
        
        EmailVerification verification = EmailVerification.builder()
                .userAccount(userAccount)
                .verificationCode(verificationCode)
                .createdAt(now)
                .expiresAt(expiresAt)
                .isVerified(false)
                .attemptCount(0)
                .maxAttempts(5)
                .build();
        
        EmailVerification savedVerification = emailVerificationRepository.save(verification);
        
        // send email with verification code
        String userName = userAccount.getUserProfile() != null ? 
                userAccount.getUserProfile().getFirstName() : userAccount.getUsername();
        String email = userAccount.getUserProfile() != null ? 
                userAccount.getUserProfile().getEmail() : userAccount.getUsername();
        
        emailService.sendVerificationEmail(email, userName, verificationCode); // Modified to use code, not link for initial verification
        
        log.info("verification code generated and sent for user: {}", userAccount.getUserAccountId());
        return savedVerification;
    }
    
    @Override
    @Transactional
    public EmailVerification verifyCode(Long userAccountId, String code) {
        if (userAccountId == null || userAccountId <= 0) {
            throw new InvalidRequestException("user account id is required");
        }
        if (code == null || code.isEmpty()) {
            throw new InvalidRequestException("verification code is required");
        }


        EmailVerification verification = emailVerificationRepository.findByUserAccountUserAccountIdAndNewEmailIsNull(userAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("no verification found for user"));    

        if (verification.getIsVerified()) {
            throw new InvalidRequestException("user is already verified");
        }
        
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) 
        {
            throw new InvalidRequestException("verification code has expired. please request a new code");
        }

        
        // check if max attempts reached
        if (verification.isMaxAttemptsReached()) {
            log.warn("max verification attempts reached for user: {}", userAccountId);
            throw new InvalidRequestException("maximum verification attempts reached. please request a new code");
        }
        
        // check if code is correct
        if (!verification.getVerificationCode().equals(code)) {
            emailVerificationRepository.incrementAttemptCount(verification.getEmailVerificationId());
            log.warn("invalid verification code provided for user: {}", userAccountId);
            throw new InvalidRequestException("invalid verification code");
        }
        
        // mark as verified
        emailVerificationRepository.markAsVerified(verification.getEmailVerificationId());
        
        // mark user account as verified
        UserAccount userAccount = verification.getUserAccount();
        userAccount.setIsVerified(true);
        userAccountRepository.save(userAccount); // Save the updated userAccount
        
        log.info("email verified for user: {}", userAccountId);
        return verification;
    }
    
    @Override
    public EmailVerification getActiveVerification(Long userAccountId) {
        if (userAccountId == null || userAccountId <= 0) {
            throw new InvalidRequestException("user account id is required");
        }
        
        return emailVerificationRepository.findActiveVerificationByUserId(userAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("no active verification found for user"));
    }
    
    @Override
    public boolean isUserVerified(Long userAccountId) {
        if (userAccountId == null || userAccountId <= 0) {
            throw new InvalidRequestException("user account id is required");
        }
        
        return emailVerificationRepository.isUserVerified(userAccountId);
    }
    
    @Override
    @Transactional
    public EmailVerification resendVerificationCode(Long userAccountId) {
        if (userAccountId == null || userAccountId <= 0) {
            throw new InvalidRequestException("user account id is required");
        }
        
        EmailVerification verification = emailVerificationRepository.findByUserAccountUserAccountIdAndNewEmailIsNull(userAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("verification not found for user"));
        
        if (verification.getIsVerified()) {
            throw new InvalidRequestException("user is already verified");
        }
        
        // delete old verification and create new one
        emailVerificationRepository.delete(verification);
        
        UserAccount userAccount = verification.getUserAccount();
        return generateAndSendVerificationCode(userAccount);
    }

    /**
     * initiates the email update verification process
     */
    @Override
    @Transactional
    public EmailVerification initiateEmailUpdate(Long userAccountId, String newEmail) {
        if (userAccountId == null || userAccountId <= 0) {
            throw new InvalidRequestException("User account ID is required");
        }
        if (newEmail == null || newEmail.isBlank()) {
            throw new InvalidRequestException("New email is required");
        }

        UserAccount userAccount = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found with ID: " + userAccountId));

        if (!userAccount.getIsVerified()) {
            throw new InvalidRequestException("User account is not verified. Please verify your current email first.");
        }

        if (userProfileRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException("Email already exists: " + newEmail);
        }

        // Revoke any pending email update verifications for this user
        emailVerificationRepository.findByUserAccountUserAccountIdAndNewEmailIsNotNull(userAccountId)
            .ifPresent(emailVerificationRepository::delete);

        String verificationCode = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(verificationExpirationMinutes);

        EmailVerification verification = EmailVerification.builder()
                .userAccount(userAccount)
                .verificationCode(verificationCode)
                .createdAt(now)
                .expiresAt(expiresAt)
                .isVerified(false)
                .attemptCount(0)
                .maxAttempts(5)
                .newEmail(newEmail) // Store the new email
                .build();

        EmailVerification savedVerification = emailVerificationRepository.save(verification);
        
        // Send email to the *current* email address of the user
        String userName = userAccount.getUserProfile() != null ? 
                userAccount.getUserProfile().getFirstName() : userAccount.getUsername();
        String currentEmail = userAccount.getUserProfile() != null ? 
                userAccount.getUserProfile().getEmail() : userAccount.getUsername();
        
        emailService.sendEmailUpdateVerificationEmail(currentEmail, userName, newEmail, verificationCode);
        
        log.info("Email update verification code generated and sent for user: {} to current email: {}", userAccountId, currentEmail);
        return savedVerification;
    }

    /**
     * confirms the email update with the provided verification code
     */
    @Override
    @Transactional
    public EmailVerification confirmEmailUpdate(Long userAccountId, String newEmail, String code) {
        if (userAccountId == null || userAccountId <= 0) {
            throw new InvalidRequestException("User account ID is required");
        }
        if (newEmail == null || newEmail.isBlank()) {
            throw new InvalidRequestException("New email is required");
        }
        if (code == null || code.isEmpty()) {
            throw new InvalidRequestException("Verification code is required");
        }

        EmailVerification verification = emailVerificationRepository.findByUserAccountUserAccountIdAndNewEmail(userAccountId, newEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No email update verification found for user or new email."));

        if (verification.getIsVerified()) {
            throw new InvalidRequestException("Email has already been updated with this verification.");
        }

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            emailVerificationRepository.delete(verification); // Clean up expired verification
            throw new InvalidRequestException("Verification code has expired. Please request a new code.");
        }

        if (verification.isMaxAttemptsReached()) {
            log.warn("Max verification attempts reached for email update for user: {}", userAccountId);
            emailVerificationRepository.delete(verification); // Clean up after max attempts
            throw new InvalidRequestException("Maximum verification attempts reached. Please request a new code.");
        }

        if (!verification.getVerificationCode().equals(code)) {
            emailVerificationRepository.incrementAttemptCount(verification.getEmailVerificationId());
            log.warn("Invalid verification code provided for email update for user: {}", userAccountId);
            throw new InvalidRequestException("Invalid verification code.");
        }

        // If code is correct, update the user's email
        userAccountService.updateUserEmail(userAccountId, newEmail);

        // Mark this specific email verification as verified
        verification.setIsVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        emailVerificationRepository.save(verification); // Save the updated verification status

        log.info("Email successfully updated for user: {} to new email: {}", userAccountId, newEmail);
        return verification;
    }
    
    @Override
    @Transactional
    public void cleanupExpiredVerifications() {
        log.info("cleaning up expired email verifications");
        emailVerificationRepository.deleteExpiredVerifications();
    }
    
    /**
     * send verification code email
     */
    private void sendVerificationCodeEmail(String email, String userName, String verificationCode) {
        String subject = "Email Verification Code - CSPS Account";
        String htmlBody = buildVerificationCodeEmailTemplate(userName, verificationCode);
        emailService.sendHtmlEmail(email, subject, htmlBody);
    }
    
    /**
     * 
     * build verification code email template
     */
   private String buildVerificationCodeEmailTemplate(String userName, String verificationCode) {
    // Split code into individual characters for the digit-box layout
    char[] digits = verificationCode.toCharArray();
    StringBuilder digitBoxes = new StringBuilder();
    for (char digit : digits) {
        digitBoxes.append("""
            <td style="width: 48px; height: 56px; text-align: center; vertical-align: middle; \
            font-size: 24px; font-weight: 700; color: #1a1a2e; background-color: #f0ecf9; \
            border: 2px solid #d6cce9; border-radius: 12px; font-family: 'Segoe UI', Arial, sans-serif; \
            letter-spacing: 0;">%c</td>
            <td style="width: 8px;"></td>
            """.formatted(digit));
    }

    return """
        <html>
        <body style="margin: 0; padding: 0; background-color: #f4f2f7; font-family: 'Segoe UI', Arial, sans-serif;">
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f2f7; padding: 40px 20px;">
                <tr>
                    <td align="center">
                        <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.06);">

                            <!-- Purple accent bar -->
                            <tr>
                                <td style="height: 4px; background: linear-gradient(90deg, #7c3aed, #a855f7);"></td>
                            </tr>

                            <!-- Content -->
                            <tr>
                                <td style="padding: 48px 40px 40px;">

                                    <!-- Heading -->
                                    <h1 style="margin: 0 0 8px; font-size: 22px; font-weight: 700; color: #1a1a2e; letter-spacing: -0.02em;">
                                        Verify your email
                                    </h1>

                                    <p style="margin: 0 0 32px; font-size: 14px; color: #6b7280; line-height: 1.6;">
                                        Hello <strong style="color: #1a1a2e;">%s</strong>, use the code below to verify your email address.
                                    </p>

                                    <!-- Code boxes -->
                                    <table role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 auto 32px;" align="center">
                                        <tr>
                                            %s
                                        </tr>
                                    </table>

                                    <!-- Expiry notice -->
                                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom: 32px;">
                                        <tr>
                                            <td style="background-color: #faf8ff; border: 1px solid #ede9fe; border-radius: 10px; padding: 16px 20px;">
                                                <p style="margin: 0 0 4px; font-size: 12px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.08em; color: #7c3aed;">
                                                    Important
                                                </p>
                                                <p style="margin: 0; font-size: 13px; color: #6b7280; line-height: 1.5;">
                                                    This code expires in <strong style="color: #1a1a2e;">5 minutes</strong> and is valid for up to 5 attempts.
                                                </p>
                                            </td>
                                        </tr>
                                    </table>

                                    <!-- Divider -->
                                    <hr style="border: none; border-top: 1px solid #f0ecf9; margin: 0 0 24px;" />

                                    <!-- Footer -->
                                    <p style="margin: 0; font-size: 12px; color: #9ca3af; line-height: 1.5;">
                                        If you didn't create a CSPS account, you can safely ignore this email.
                                    </p>

                                </td>
                            </tr>
                        </table>

                        <!-- Brand footer -->
                        <p style="margin: 24px 0 0; font-size: 11px; color: #9ca3af; letter-spacing: 0.05em;">
                            CSPS &mdash; Computer Studies and Programming Society
                        </p>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """.formatted(userName, digitBoxes.toString());
}
}
