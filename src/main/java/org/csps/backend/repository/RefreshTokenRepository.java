package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.RefreshToken;
import org.csps.backend.domain.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String token);
    void deleteByUserAccount(UserAccount userAccount);
    Optional<RefreshToken> findByUserAccount(UserAccount userAccount);
}
