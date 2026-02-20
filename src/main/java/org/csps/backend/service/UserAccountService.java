package org.csps.backend.service;

import java.util.Optional;

import org.csps.backend.domain.entities.UserAccount;


public interface UserAccountService {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findById(Long id);

    /**
     * Updates the email address for a given user account.
     * @param userAccountId The ID of the user account.
     * @param newEmail The new email address.
     * @return The updated UserAccount.
     */
    UserAccount updateUserEmail(Long userAccountId, String newEmail);
}
