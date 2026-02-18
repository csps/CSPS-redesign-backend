package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.RecoveryToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface RecoveryTokenRepository extends JpaRepository<RecoveryToken, Long> {
    
    /**
     * find recovery token by token string
     */
    Optional<RecoveryToken> findByToken(String token);
    
    /**
     * find recovery token by user account id (most recent, unused)
     */
    @Query("SELECT rt FROM RecoveryToken rt WHERE rt.userAccount.userAccountId = :userAccountId " +
           "AND rt.isUsed = false AND rt.expiresAt > CURRENT_TIMESTAMP " +
           "ORDER BY rt.createdAt DESC LIMIT 1")
    Optional<RecoveryToken> findValidTokenByUserAccountId(@Param("userAccountId") Long userAccountId);
    
    /**
     * mark token as used
     */
    @Modifying
    @Transactional
    @Query("UPDATE RecoveryToken rt SET rt.isUsed = true, rt.usedAt = CURRENT_TIMESTAMP " +
           "WHERE rt.recoveryTokenId = :recoveryTokenId")
    void markAsUsed(@Param("recoveryTokenId") Long recoveryTokenId);
    
    /**
     * delete expired tokens
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RecoveryToken rt WHERE rt.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
    
    /**
     * count valid tokens for user
     */
    @Query("SELECT COUNT(rt) FROM RecoveryToken rt WHERE rt.userAccount.userAccountId = :userAccountId " +
           "AND rt.isUsed = false AND rt.expiresAt > CURRENT_TIMESTAMP")
    long countValidTokens(@Param("userAccountId") Long userAccountId);
}
