package org.csps.backend.repository;

import java.util.Optional;

import org.csps.backend.domain.entities.Admin;
import org.csps.backend.domain.enums.AdminPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserAccountUserAccountId(Long userAccountId);
    boolean existsByPosition(AdminPosition position);

}
