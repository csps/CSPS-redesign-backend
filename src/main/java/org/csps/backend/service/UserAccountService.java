package org.csps.backend.service;

import java.util.Optional;

import org.csps.backend.domain.entities.UserAccount;


public interface UserAccountService {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findById(Long id);
}
