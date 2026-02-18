package org.csps.backend.controller;

import org.csps.backend.domain.dtos.request.EmailVerificationRequestDTO;
import org.csps.backend.domain.dtos.response.EmailVerificationResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.mapper.EmailVerificationMapper;
import org.csps.backend.service.EmailVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/email-verification")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final EmailVerificationMapper emailVerificationMapper;

    /**
     * Send verification code to user's email.
     * Initiates the email verification process for authenticated students and admins.
     */
    @PostMapping("/send")
    public ResponseEntity<GlobalResponseBuilder<EmailVerificationResponseDTO>> sendVerificationCode(
            Authentication authentication) {
        
        Long userAccountId = (Long) authentication.getCredentials();
        var verification = emailVerificationService.sendVerificationCode(userAccountId);
        EmailVerificationResponseDTO responseDTO = emailVerificationMapper.toResponseDTO(verification);
        
        return GlobalResponseBuilder.buildResponse(
            "Verification code sent successfully",
            responseDTO,
            HttpStatus.OK
        );
    }

    /**
     * Verify email with the provided verification code.
     * Only for authenticated students and admins.
     */
    @PostMapping("/verify")
    public ResponseEntity<GlobalResponseBuilder<EmailVerificationResponseDTO>> verifyEmail(
            Authentication authentication,
            @Valid @RequestBody EmailVerificationRequestDTO requestDTO) {
        
        try {
            Long userAccountId = (Long) authentication.getCredentials();


            var verification = emailVerificationService.verifyCode(userAccountId, requestDTO.getCode());
            EmailVerificationResponseDTO responseDTO = emailVerificationMapper.toResponseDTO(verification);
            
            return GlobalResponseBuilder.buildResponse(
                "Email verified successfully",
                responseDTO,
                HttpStatus.OK
            );
        } catch (NumberFormatException e) {
            return GlobalResponseBuilder.buildResponse(
                "Invalid user ID format",
                null,
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Resend verification code to user's email.
     * Only for authenticated students and admins.
     */
    @PostMapping("/resend")
    public ResponseEntity<GlobalResponseBuilder<EmailVerificationResponseDTO>> resendVerificationCode(
            Authentication authentication) {
    
        Long userAccountId = (Long) authentication.getCredentials();
        
        var verification = emailVerificationService.resendVerificationCode(userAccountId);
        EmailVerificationResponseDTO responseDTO = emailVerificationMapper.toResponseDTO(verification);
        
        return GlobalResponseBuilder.buildResponse(
            "Verification code sent successfully",
            responseDTO,
            HttpStatus.OK
        );
    }

    /**
     * Get active verification record for a user.
     * Only for authenticated students and admins.
     */
    @GetMapping("/active")
    public ResponseEntity<GlobalResponseBuilder<EmailVerificationResponseDTO>> getActiveVerification(
            Authentication authentication) {
        
        Long userAccountId = (Long) authentication.getCredentials();
        var verification = emailVerificationService.getActiveVerification(userAccountId);
        EmailVerificationResponseDTO responseDTO = emailVerificationMapper.toResponseDTO(verification);
        
        return GlobalResponseBuilder.buildResponse(
            "Active verification retrieved successfully",
            responseDTO,
            HttpStatus.OK
        );
    }

    /**
     * Check if a user's email is verified.
     * Only for authenticated students and admins.
     */
    @GetMapping("/is-verified")
    public ResponseEntity<GlobalResponseBuilder<Boolean>> isUserVerified(
            Authentication authentication) {
        
        Long userAccountId = (Long) authentication.getCredentials();
        boolean isVerified = emailVerificationService.isUserVerified(userAccountId);
        
        return GlobalResponseBuilder.buildResponse(
            "Verification status retrieved successfully",
            isVerified,
            HttpStatus.OK
        );
    }
}
