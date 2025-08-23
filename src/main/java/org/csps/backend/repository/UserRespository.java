package org.csps.backend.repository;

import org.csps.backend.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRespository extends JpaRepository<User, Long> {
}
