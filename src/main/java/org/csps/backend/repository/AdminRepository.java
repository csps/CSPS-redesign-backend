package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.Admin;
import org.csps.backend.domain.enums.AdminPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.userAccount ua LEFT JOIN FETCH ua.userProfile WHERE a.userAccount.userAccountId = :accountId")
    Optional<Admin> findByUserAccountUserAccountId(@Param("accountId") Long userAccountId);
    
    boolean existsByPosition(AdminPosition position);
    boolean existsByUserAccountUserAccountId(Long userAccountId);
    
    /* find admin by user profile id (to check if a student is already an admin) */
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.userAccount ua LEFT JOIN FETCH ua.userProfile WHERE ua.userProfile.userId = :userId")
    Optional<Admin> findByUserAccount_UserProfile_UserId(@Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Admin a WHERE a.userAccount.userProfile.userId = :userId")
    boolean existsByUserAccount_UserProfile_UserId(Long userId);

    /* efficient query to get admin position for JWT generation without full entity fetch */
    @Query("SELECT a.position FROM Admin a WHERE a.userAccount.userAccountId = :accountId")
    Optional<AdminPosition> findPositionByUserAccountId(@Param("accountId") Long accountId);
}
