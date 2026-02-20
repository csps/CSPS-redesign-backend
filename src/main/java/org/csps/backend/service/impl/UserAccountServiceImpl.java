package org.csps.backend.service.impl;

import java.util.Optional;

import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.exception.EmailAlreadyExistsException;
import org.csps.backend.exception.ResourceNotFoundException;
import org.csps.backend.repository.UserAccountRepository;
import org.csps.backend.repository.UserProfileRepository;
import org.csps.backend.service.UserAccountService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return Optional.ofNullable(userAccountRepository.findByUsername(username));
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        return userAccountRepository.findById(id);
    }

    /**
     * Updates the email address for a given user account.
     * @param userAccountId The ID of the user account.
     * @param newEmail The new email address.
     * @return The updated UserAccount.
     */
    @Override
    @Transactional
    public UserAccount updateUserEmail(Long userAccountId, String newEmail) {
        UserAccount userAccount = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAccount not found with ID: " + userAccountId));

        // Check if the new email is already taken by another user
        if (userProfileRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException("Email already exists: " + newEmail);
        }

        userAccount.getUserProfile().setEmail(newEmail);
        return userAccountRepository.save(userAccount);
    }
}
