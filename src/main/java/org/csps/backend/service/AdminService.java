package org.csps.backend.service;

import java.util.Optional;

import org.csps.backend.domain.entities.Admin;

public interface AdminService {
    Optional<Admin> findByAccountId(Long accountId);
    Optional<Admin> findById (Long Id);
}
