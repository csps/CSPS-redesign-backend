package org.csps.backend.controller;

import org.csps.backend.domain.dtos.request.SignInCredentialRequestDTO;
import org.csps.backend.domain.dtos.response.AdminResponseDTO;
import org.csps.backend.domain.dtos.response.AuthResponseDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.security.JwtService;
import org.csps.backend.service.AdminService;
import org.csps.backend.service.RefreshTokenService;
import org.csps.backend.service.StudentService;
import org.csps.backend.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAccountService userService;

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final StudentService studentService;
    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<GlobalResponseBuilder<AuthResponseDTO>> login(@Valid @RequestBody SignInCredentialRequestDTO signInRequest) {
        String studentId = signInRequest.getStudentId();
        
        UserAccount user = userService.findByUsername(studentId)
                .orElse(null);

        if (user == null || !user.getPassword().equals(signInRequest.getPassword())) {
            return GlobalResponseBuilder.buildResponse(
                "Invalid credentials",
                null,
                HttpStatus.UNAUTHORIZED
            );
        }
        
        Long userAccountId = user.getUserAccountId();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(userAccountId).getRefreshToken();

        // Return both tokens in response body - client should store in memory or secure storage
        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .build();

        return GlobalResponseBuilder.buildResponse(
            "Login successful",
            authResponse,
            HttpStatus.OK
        );
    }

    // logout
    @PostMapping("/logout")
    public ResponseEntity<GlobalResponseBuilder<String>> logout(@RequestBody(required = false) Map<String, String> requestBody) {
        String refreshToken = null;
        if (requestBody != null && requestBody.containsKey("refreshToken")) {
            refreshToken = requestBody.get("refreshToken");
        }
        
        // Delete refresh token from database if it exists
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.findByRefreshToken(refreshToken)
                .ifPresent(refreshTokenService::deleteRefreshToken);
        }
        
        return GlobalResponseBuilder.buildResponse(
            "Logout successful",
            null,
            HttpStatus.OK
        );
    }

    // refresh token
    @PostMapping("/refresh")
    public ResponseEntity<GlobalResponseBuilder<AuthResponseDTO>> refresh(
        @RequestBody Map<String, String> requestBody
    ) {
        String requestToken = requestBody.get("refreshToken");
        if (requestToken == null || requestToken.isBlank()) {
            return GlobalResponseBuilder.buildResponse(
                "Refresh token is missing",
                null,
                HttpStatus.BAD_REQUEST
            );
        }

        var result = refreshTokenService.refreshAccessToken(requestToken);
        if (result.isPresent()) {
            String newAccessToken = result.get();
            
            return GlobalResponseBuilder.buildResponse(
                "Access token refreshed successfully",
                new AuthResponseDTO(newAccessToken),
                HttpStatus.OK
            );
        } else {
            return GlobalResponseBuilder.buildResponse(
                "Invalid or expired refresh token",
                null,
                HttpStatus.UNAUTHORIZED
            );
        }
    }

    // get student profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> studentProfile(@AuthenticationPrincipal String studentId) {
        // get student by id
        StudentResponseDTO student = studentService.findById(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"));
        return ResponseEntity.ok(student);
    }

    @GetMapping("/admin/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminProfile(@AuthenticationPrincipal Long adminId) {
        // get admin by id
        AdminResponseDTO admin = adminService.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
        // return admin
        return ResponseEntity.ok(admin);
    }

    
}