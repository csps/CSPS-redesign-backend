package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.csps.backend.domain.enums.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    
    @Query("SELECT ua FROM UserAccount ua LEFT JOIN FETCH ua.userProfile WHERE ua.username = :username")
    UserAccount findByUsername(@Param("username") String username);
    
    boolean existsByUsername(String username);
    
    Optional<UserAccount> findByUserProfile(UserProfile userProfile);
    
    Optional<UserAccount> findByUserProfileAndRole(UserProfile userProfile, UserRole role);
}
