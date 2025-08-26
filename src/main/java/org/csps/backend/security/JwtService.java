package org.csps.backend.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.csps.backend.domain.dtos.request.SignInCredentialRequestDTO;
import org.csps.backend.domain.entities.Admin;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.enums.UserRole;
import org.csps.backend.service.AdminService;
import org.csps.backend.service.StudentService;
import org.csps.backend.service.UserAccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${csps.jwtToken.secretKey}")
    private String secretKey;
    @Value("${csps.jwtAccessToken.expireMs}")
    private long jwtAccessTokenExpirationMs;

    private final UserAccountService userAccountService;

    private final StudentService studentService;

    private final AdminService adminService;

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUsernameId (String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    public Date getExpiration (String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Boolean isTokenExpired (String token) {
        return getExpiration(token).before(new Date());
    }

    private String generateAccessToken (Map<String, Object> customClaim,  SignInCredentialRequestDTO studentRequest) {
        UserAccount account = userAccountService.findByUsername(studentRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("UserAccount not found"));

        String username = account.getUsername();
        String role = account.getRole().toString();

        customClaim.put("username", username);
        customClaim.put("role", role);
        customClaim.put("profileId", account.getUserProfile().getUserId());

        if (account.getRole() == UserRole.STUDENT) {
            Student student = studentService.findByAccountId(account.getUserAccountId())
                    .orElseThrow(() -> new RuntimeException("Student record not found"));
            customClaim.put("studentId", student.getStudentId());
            customClaim.put("yearLevel", student.getYearLevel());
        } else if (account.getRole() == UserRole.ADMIN) {
            Admin admin = adminService.findByAccountId(account.getUserAccountId())
                    .orElseThrow(() -> new RuntimeException("Admin record not found"));
            customClaim.put("adminId", admin.getAdminId());
            customClaim.put("position", admin.getPosition().name());
        }

        // 4. Generate token
        return Jwts.builder()
                .claims(customClaim)
                .subject(String.valueOf(account.getUserAccountId()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtAccessTokenExpirationMs))
                .signWith(getSignInKey())
                .compact();
    }
    

    public String generateAccessToken(SignInCredentialRequestDTO studentRequest) {
        return generateAccessToken(new HashMap<String, Object>(), studentRequest);
    }

    
    public Boolean isTokenValid (String token, SignInCredentialRequestDTO studentRequest) {
        final Long usernameId = extractUsernameId(token);

        UserAccount userEntity = userAccountService.findByUsername(studentRequest.getUsername()).
                                      orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return (usernameId.equals(userEntity.getUserAccountId()) && !isTokenExpired(token));
    }


}
