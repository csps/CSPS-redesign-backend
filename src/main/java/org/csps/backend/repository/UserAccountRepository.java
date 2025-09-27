package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.csps.backend.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<UserAccount> findByUserProfile(UserProfile userProfile);
    Optional<UserAccount> findByUserProfileAndRole(UserProfile userProfile, UserRole role);

    
}
