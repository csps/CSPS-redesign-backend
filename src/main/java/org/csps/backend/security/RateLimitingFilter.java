package org.csps.backend.security;

import java.io.IOException;

import org.csps.backend.configs.RateLimitingConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Rate limiting filter to prevent DDoS attacks
 * Uses Bucket4j token bucket algorithm
 * Applies limits per client IP address
 */
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitingConfig rateLimitingConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        /* skip rate limiting if disabled */
        if (!rateLimitingConfig.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        /* skip rate limiting for excluded paths */
        if (rateLimitingConfig.isPathExcluded(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        /* get client IP for rate limiting */
        String clientId = getClientIp(request);
        
        /* get or create bucket for this client */
        Bucket bucket = rateLimitingConfig.resolveBucket(clientId);
        
        /* consume a token from bucket */
        if (bucket.tryConsume(1)) {
            /* token available - request allowed */
            response.addHeader("X-Rate-Limit-Limit", String.valueOf(rateLimitingConfig.getRequestsPerMinute()));
            filterChain.doFilter(request, response);
        } else {
            /* no token available - reject request with 429 Too Many Requests */
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", "60");
            response.setStatus(429); /* HTTP 429 Too Many Requests */
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please retry after 60 seconds.\"}");
        }
    }

    /* extract client IP from request, considering proxies */
    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }
        
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        
        /* handle X-Forwarded-For which can contain multiple IPs */
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        
        return clientIp;
    }
}
