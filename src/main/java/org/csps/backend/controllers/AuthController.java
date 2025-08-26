package org.csps.backend.controllers;

import java.util.Map;

import org.csps.backend.domain.dtos.request.SignInCredentialRequestDTO;
import org.csps.backend.domain.dtos.response.JwtTokenResponseDto;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.csps.backend.security.JwtService;
import org.csps.backend.security.UserPrincipal;
import org.csps.backend.service.AdminService;
import org.csps.backend.service.RefreshTokenService;
import org.csps.backend.service.StudentService;
import org.csps.backend.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<?> login(@RequestBody SignInCredentialRequestDTO request) {
        String usernameRequest = request.getUsername();

        UserAccount user = userService.findByUsername(usernameRequest).
                                orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }        

        String token = jwtService.generateAccessToken(request);
        String refreshToken = refreshTokenService.createRefreshToken(user.getUserAccountId()).getRefreshToken();

        return ResponseEntity.ok().body(new JwtTokenResponseDto(token, refreshToken));
    }

    
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");

        if (requestToken == null || requestToken.isBlank()) {
            return ResponseEntity.badRequest().body("Missing refresh token");
        }

        return refreshTokenService.refreshAccessToken(requestToken)
                .map(newAccessToken -> ResponseEntity.ok(Map.of("accessToken", newAccessToken)))
                .orElseGet(() ->ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired refresh token")));
    }
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> studentProfile(@AuthenticationPrincipal String studentId) {
        var student = studentService.findById(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found"));
        return ResponseEntity.ok(student);
    }

    @GetMapping("/admin/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminProfile(@AuthenticationPrincipal Long adminId) {
        var admin = adminService.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
        return ResponseEntity.ok(admin);
    }

}
