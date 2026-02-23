package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    
    /**
     * find email verification by user account id where newEmail is null (initial verification)
     */
    Optional<EmailVerification> findByUserAccountUserAccountIdAndNewEmailIsNull(Long userAccountId);

    /**
     * find email verification by user account id where newEmail is not null (email update verification)
     */
    Optional<EmailVerification> findByUserAccountUserAccountIdAndNewEmailIsNotNull(Long userAccountId);

    /**
     * find email verification by user account id and newEmail for update verification
     */
    Optional<EmailVerification> findByUserAccountUserAccountIdAndNewEmail(Long userAccountId, String newEmail);
    
    /**
     * find active verification code for verification
     */
    @Query("SELECT ev FROM EmailVerification ev WHERE ev.userAccount.userAccountId = :userAccountId " +
           "AND ev.isVerified = false AND ev.expiresAt > CURRENT_TIMESTAMP AND ev.attemptCount < ev.maxAttempts")
    Optional<EmailVerification> findActiveVerificationByUserId(@Param("userAccountId") Long userAccountId);
    
    /**
     * mark verification as verified
     */
    @Modifying
    @Transactional
    @Query("UPDATE EmailVerification ev SET ev.isVerified = true, ev.verifiedAt = CURRENT_TIMESTAMP " +
           "WHERE ev.emailVerificationId = :emailVerificationId")
    void markAsVerified(@Param("emailVerificationId") Long emailVerificationId);
    
    /**
     * increment attempt count
     */
    @Modifying
    @Transactional
    @Query("UPDATE EmailVerification ev SET ev.attemptCount = ev.attemptCount + 1 " +
           "WHERE ev.emailVerificationId = :emailVerificationId")
    void incrementAttemptCount(@Param("emailVerificationId") Long emailVerificationId);
    
    /**
     * delete expired email verifications
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerification ev WHERE ev.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredVerifications();
    
    /**
     * check if user is already verified
     */
    @Query("SELECT CASE WHEN COUNT(ev) > 0 THEN true ELSE false END " +
           "FROM EmailVerification ev WHERE ev.userAccount.userAccountId = :userAccountId AND ev.isVerified = true")
    boolean isUserVerified(@Param("userAccountId") Long userAccountId);
}
