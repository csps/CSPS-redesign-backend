package org.csps.backend.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.enums.AdminPosition;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final UserAccount user;  // underlying user entity
    private final String domainId;   // role-specific identifier (studentId/adminId)
    private final String role;       // role name (STUDENT / ADMIN)
    private final AdminPosition position; 

    // Expose domainId directly
    public String getDomainId() {
        return domainId;
    }

    // Return adminId if role = ADMIN
    public Long getAdminId() {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return Long.valueOf(domainId);
        }
        return null;
    }

    // Return studentId if role = STUDENT
    public String getStudentId() {
        if ("STUDENT".equalsIgnoreCase(role)) {
            return domainId;
        }
        return null;
    }

    // Spring Security: grant role authority (e.g., ROLE_ADMIN, ROLE_STUDENT)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (getRole().equalsIgnoreCase("ADMIN")) {

            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            switch(position) {
                case PRESIDENT, VP_INTERNAL, VP_EXTERNAL, SECRETARY -> {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_EXECUTIVE"));
                }
                case TREASURER, ASSISTANT_TREASURER -> {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_FINANCE"));
                }
            }

            return authorities;
        }

        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole()));
        
    }

    public String getPosition() {
        return position != null ? position.name() : null;
    }

    // Delegate to UserAccount for credentials
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // Default: account is always active/valid
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
