package org.csps.backend.service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.csps.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QRTokenService {

    private final JwtService jwtService;

    @Value("${csps.jwtToken.secretKey}")
    private String secretKey;

    private static final long QR_TOKEN_EXPIRATION_MS = 86400000; // 24 hours

    /* decode secret key for HMAC-SHA */
    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    /* generate QR token with session id and student id extracted from JWT token */
    public String generateQRToken(Long sessionId, String studentToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sessionId", sessionId);

        /* extract student id from the authenticated JWT token if provided */
        if (studentToken != null && !studentToken.isEmpty()) {
            String extractedStudentId = jwtService.getStudentIdFromToken(studentToken);
            if (extractedStudentId != null) {
                claims.put("studentId", extractedStudentId);
            }
        }

        return Jwts.builder()
                .claims(claims)
                .subject("qr-token")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + QR_TOKEN_EXPIRATION_MS))
                .signWith(getSignInKey())
                .compact();
    }

    /* validate QR token signature and extract claims */
    public Claims validateAndExtractClaims(String qrToken) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(qrToken)
                    .getPayload();
        } catch (Exception ex) {
            return null; // Token is invalid or expired
        }
    }

    /* extract session id from QR token */
    public Long extractSessionId(String qrToken) {
        Claims claims = validateAndExtractClaims(qrToken);
        if (claims == null) {
            return null;
        }
        return claims.get("sessionId", Long.class);
    }

    /* extract student id from QR token */
    public String extractStudentId(String qrToken) {
        Claims claims = validateAndExtractClaims(qrToken);
        if (claims == null) {
            return null;
        }
        return claims.get("studentId", String.class);
    }

    /* check if QR token is valid */
    public boolean isQRTokenValid(String qrToken) {
        return validateAndExtractClaims(qrToken) != null;
    }

    /* check if QR token is expired by checking expiration date */
    public boolean isQRTokenExpired(String qrToken) {
        Claims claims = validateAndExtractClaims(qrToken);
        if (claims == null) {
            return true; // Invalid token is considered expired
        }
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
