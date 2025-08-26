package org.csps.backend.security;

import java.util.Collection;
import java.util.List;

import org.csps.backend.domain.entities.UserAccount;
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

    private final UserAccount user;
    private final String domainId; 
    private final String role;

    public String getDomainId() {
        return domainId;
    }

    public Long getAdminId() {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return Long.valueOf(domainId);
        }
        return null;
    }

    public String getStudentId() {
        if ("STUDENT".equalsIgnoreCase(role)) {
            return domainId;
        }
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
