package org.csps.backend.security;

import org.csps.backend.domain.dtos.request.SignInCredentialRequestDTO;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.service.UserAccountService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserAccountService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, java.io.IOException {

        // Initialize token
        String accessToken = null;        

        try {
            // Find the "accessToken" in the cookie
            if (request.getCookies() != null) {
                for (var cookie : request.getCookies()) {
                    if (cookie.getName().equals("accessToken")) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }
            
            if (accessToken == null) {
                // No token found, continue filter chain
                filterChain.doFilter(request, response);
                return;
            }

            final Long userId = jwtService.extractUsernameId(accessToken); // Parse userId from token

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Authenticate only if no authentication exists
            if (auth == null) {
                // Load user from DB
                UserAccount user = userService.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // Validate token
                if (!jwtService.isTokenValid(accessToken, user)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token");
                    return;
                }

                // If token is valid
                
                // Load principal (with role + authorities)
                UserPrincipal userPrincipal =
                (UserPrincipal) customUserDetailsService.loadUserByUsername(user.getUsername());
                
                // Decide which domainId to use (Student/Admin)
                Object domainId;
                if ("STUDENT".equalsIgnoreCase(userPrincipal.getRole())) {
                    domainId = userPrincipal.getStudentId();
                } else if ("ADMIN".equalsIgnoreCase(userPrincipal.getRole())) {
                    domainId = userPrincipal.getAdminId();
                } else {
                    throw new RuntimeException("Role not recognized: " + userPrincipal.getRole());
                }                
                // Build authentication object
                UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                                domainId,
                                null,
                                userPrincipal.getAuthorities()
                        );

                // Attach request details and set authentication
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
            // Handle invalid/expired token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        // Continue request filter chain
        filterChain.doFilter(request, response);
    }
}
