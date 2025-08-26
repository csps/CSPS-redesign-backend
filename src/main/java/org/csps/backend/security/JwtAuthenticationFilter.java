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
    private final CustomUserDetailsService customUserDetailsService; // integrate your user details service

@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, java.io.IOException {

    // 1. Get the Authorization header
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);

        return;
    }

    try {
        final String token = authHeader.substring(7).trim();
        final Long userId = jwtService.extractUsernameId(token);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            UserAccount user = userService.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
            SignInCredentialRequestDTO requestCredential = new SignInCredentialRequestDTO(user.getUsername(), user.getPassword());
            
            if (!jwtService.isTokenValid(token, requestCredential)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
            
            Object domainId = null;
            UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService.loadUserByUsername(user.getUsername());

            switch(userPrincipal.getRole()) {
                case "STUDENT" -> {domainId = userPrincipal.getStudentId();}
                case "ADMIN" -> {domainId = userPrincipal.getAdminId();}
                default -> throw new RuntimeException("Role not recognized");                
            }
            
            UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        domainId, 
                        null,
                        userPrincipal.getAuthorities()
            );
            


            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

    }   
    catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            
            return;
        }

        filterChain.doFilter(request, response);

    }

}
