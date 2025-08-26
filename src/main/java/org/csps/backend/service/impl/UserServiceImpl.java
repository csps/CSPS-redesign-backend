package org.csps.backend.service.impl;

import java.util.Optional;

import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.repository.UserAccountRepository;
import org.csps.backend.service.UserAccountService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return Optional.ofNullable(userAccountRepository.findByUsername(username));
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        return userAccountRepository.findById(id);
    }

}
