package org.csps.backend.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.enums.AdminPosition;
import org.csps.backend.domain.enums.UserRole;
import org.csps.backend.repository.AdminRepository;
import org.csps.backend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
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
    private String secretKey; // Base64-encoded secret key

    private long jwtAccessTokenExpirationMs = 90000000; // Expiration time in ms

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    // Decode secret key into HMAC-SHA key
    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    // Extract all claims (payload) from JWT
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract userAccountId (subject) from token
    public Long extractUsernameId(String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    // Get expiration date from token
    public Date getExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // Check if token is expired
    private Boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }


    public String getStudentIdFromToken(String token) {
        return extractAllClaims(token).get("studentId", String.class);
    }

    // Core method: build JWT with claims depending on role (Student/Admin)
    private String generateAccessToken(Map<String, Object> customClaim, UserAccount user, String domainId, String position) {
        /* Generates JWT with optional domainId/position to avoid extra DB queries during login */

        // Add base claims
        customClaim.put("role", user.getRole().toString());


        boolean middleNameExists = user.getUserProfile().getMiddleName() != null && !user.getUserProfile().getMiddleName().isEmpty();
        String fullName = user.getUserProfile().getFirstName() + (middleNameExists ? " " + user.getUserProfile().getMiddleName() : "") + " " + user.getUserProfile().getLastName();
        customClaim.put("fullName", fullName);

        // Add role-specific claims
        if (user.getRole() == UserRole.STUDENT) {
            if (domainId != null) {
                customClaim.put("studentId", domainId);
            } else {
                /* efficient query to get student ID directly */
                String studentId = studentRepository.findStudentIdByUserAccountId(user.getUserAccountId())
                        .orElseThrow(() -> new RuntimeException("Student record not found"));
                customClaim.put("studentId", studentId);
            }
        } else if (user.getRole() == UserRole.ADMIN) {
            if (position != null) {
                customClaim.put("position", position);
            } else {
                /* efficient query to get admin position directly */
                AdminPosition adminPosition = adminRepository.findPositionByUserAccountId(user.getUserAccountId())
                        .orElseThrow(() -> new RuntimeException("Admin record not found"));
                customClaim.put("position", adminPosition.name());
            }
        }

    
        // Generate and sign token
        return Jwts.builder()
                .claims(customClaim)
                .subject(String.valueOf(user.getUserAccountId())) // subject = accountId
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtAccessTokenExpirationMs))
                .signWith(getSignInKey())
                .compact();
    }

    // Generate token with empty claims
    public String generateAccessToken(UserAccount user) {
        return generateAccessToken(new HashMap<>(), user, null, null);
    }

    // Generate token with optional domainId/position to avoid extra queries during login
    public String generateAccessToken(UserAccount user, String domainId, String position) {
        return generateAccessToken(new HashMap<>(), user, domainId, position);
    }

    public Long getUserIdFromToken(String token) {
        return extractUsernameId(token);
    }

    // Validate token: check subject matches and not expired
    public Boolean isTokenValid(String token, UserAccount user) {
        final Long usernameId = extractUsernameId(token);

        return (usernameId.equals(user.getUserAccountId()) && !isTokenExpired(token));
    }
}
