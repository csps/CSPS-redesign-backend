package org.csps.backend.controller;

import org.csps.backend.domain.dtos.request.PasswordRecoveryRequestDTO;
import org.csps.backend.domain.dtos.request.PasswordResetRequestDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.RecoveryTokenResponseDTO;
import org.csps.backend.domain.entities.RecoveryToken;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.mapper.RecoveryTokenMapper;
import org.csps.backend.service.RecoveryTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/recovery-token")
@RequiredArgsConstructor
@Slf4j
public class RecoveryTokenController {

    private final RecoveryTokenService recoveryTokenService;
    private final RecoveryTokenMapper recoveryTokenMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Request password recovery - sends recovery token via email
     * User must have verified email to request recovery
     */
    @PostMapping("/request")
    public ResponseEntity<GlobalResponseBuilder<RecoveryTokenResponseDTO>> requestRecovery(
            @Valid @RequestBody PasswordRecoveryRequestDTO requestDTO) {
        
        try {
            /* generate recovery token */
            RecoveryToken recoveryToken = recoveryTokenService.generateRecoveryToken(requestDTO);
            
            RecoveryTokenResponseDTO responseDTO = recoveryTokenMapper.toResponseDTO(recoveryToken);
            return GlobalResponseBuilder.buildResponse(
                "Recovery token sent to email",
                responseDTO,
                HttpStatus.OK
            );
        } catch (org.csps.backend.exception.ResourceNotFoundException e) {
            return GlobalResponseBuilder.buildResponse(
                e.getMessage(),
                null,
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("failed to request recovery: {}", e.getMessage(), e);
            return GlobalResponseBuilder.buildResponse(
                "Failed to process recovery request",
                null,
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Validate recovery token - check if token is valid and not expired
     */
    @PostMapping("/validate")
    public ResponseEntity<GlobalResponseBuilder<RecoveryTokenResponseDTO>> validateToken(
            @RequestBody java.util.Map<String, String> request) {
        
        try {
            String token = request.get("token");
            if (token == null || token.isBlank()) {
                return GlobalResponseBuilder.buildResponse(
                    "Recovery token is required",
                    null,
                    HttpStatus.BAD_REQUEST
                );
            }
            
            /* validate token */
            RecoveryToken recoveryToken = recoveryTokenService.validateRecoveryToken(token);
            
            RecoveryTokenResponseDTO responseDTO = recoveryTokenMapper.toResponseDTO(recoveryToken);
            return GlobalResponseBuilder.buildResponse(
                "Recovery token is valid",
                responseDTO,
                HttpStatus.OK
            );
        } catch (org.csps.backend.exception.ResourceNotFoundException e) {
            return GlobalResponseBuilder.buildResponse(
                "Recovery token not found",
                null,
                HttpStatus.NOT_FOUND
            );
        } catch (org.csps.backend.exception.InvalidRequestException e) {
            return GlobalResponseBuilder.buildResponse(
                e.getMessage(),
                null,
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Reset password using recovery token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<GlobalResponseBuilder<String>> resetPassword(
            @Valid @RequestBody PasswordResetRequestDTO requestDTO) {
        
        try {
            /* check if passwords match */
            if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmPassword())) {
                return GlobalResponseBuilder.buildResponse(
                    "Passwords do not match",
                    null,
                    HttpStatus.BAD_REQUEST
                );
            }
            
            /* validate recovery token */
            RecoveryToken recoveryToken = recoveryTokenService.validateRecoveryToken(requestDTO.getToken());
            
            /* check if token already used */
            if (recoveryToken.getIsUsed()) {
                return GlobalResponseBuilder.buildResponse(
                    "Recovery token has already been used",
                    null,
                    HttpStatus.BAD_REQUEST
                );
            }
            
            /* reset password */
            UserAccount userAccount = recoveryToken.getUserAccount();
            
            String encodedPassword = passwordEncoder.encode(requestDTO.getNewPassword());
            userAccount.setPassword(encodedPassword);
            
            /* mark token as used */
            recoveryTokenService.markTokenAsUsed(requestDTO.getToken());
            
            log.info("password reset successfully for user: {}", userAccount.getUserAccountId());
            
            return GlobalResponseBuilder.buildResponse(
                "Password reset successfully",
                null,
                HttpStatus.OK
            );
        } catch (org.csps.backend.exception.ResourceNotFoundException e) {
            return GlobalResponseBuilder.buildResponse(
                "Recovery token not found or expired",
                null,
                HttpStatus.NOT_FOUND
            );
        } catch (org.csps.backend.exception.InvalidRequestException e) {
            return GlobalResponseBuilder.buildResponse(
                e.getMessage(),
                null,
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("failed to reset password: {}", e.getMessage(), e);
            return GlobalResponseBuilder.buildResponse(
                "Failed to reset password",
                null,
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

 
}
