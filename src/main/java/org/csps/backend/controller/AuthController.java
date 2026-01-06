package org.csps.backend.controller;

import org.springframework.http.HttpHeaders;
import java.util.Map;


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
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final String REFRESH_PATH = "/api/auth/refresh";
    private final UserAccountService userService;

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final StudentService studentService;
    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<GlobalResponseBuilder<AuthResponseDTO>> login(@Valid @RequestBody SignInCredentialRequestDTO signInRequest, HttpServletResponse response) {
        String studentId = signInRequest.getStudentId();
        

        UserAccount user = userService.findByUsername(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Long userAccountId = user.getUserAccountId();

        if (!user.getPassword().equals(signInRequest.getPassword())) 
            return GlobalResponseBuilder.buildResponse(
                "Invalid credentials",
                null,
                HttpStatus.UNAUTHORIZED
            );
        
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(userAccountId).getRefreshToken();


        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path(REFRESH_PATH)
                .sameSite("Strict")
                .maxAge(30 * 24 * 60 * 60) // 30 days
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        AuthResponseDTO authResponse = new AuthResponseDTO(accessToken);

        return GlobalResponseBuilder.buildResponse(
            "Login successful",
            authResponse,
            HttpStatus.OK
        );
    }


    // refresh token
    @PostMapping("/refresh")
    public ResponseEntity<GlobalResponseBuilder<AuthResponseDTO>> refresh(
        @CookieValue(name = "refreshToken", required = false) String requestToken
    ) {
        if (requestToken == null || requestToken.isBlank()) {
            return GlobalResponseBuilder.buildResponse(
                "Refresh token is missing",
                null,
                HttpStatus.BAD_REQUEST
            );
        }

        return refreshTokenService.refreshAccessToken(requestToken)
                .map(newAccessToken -> GlobalResponseBuilder.buildResponse(
                    "Access token refreshed successfully",
                    new AuthResponseDTO(newAccessToken),
                    HttpStatus.OK
                ))
                .orElseGet(() -> GlobalResponseBuilder.buildResponse(
                    "Invalid or expired refresh token",
                    null,
                    HttpStatus.UNAUTHORIZED
                ));
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