package org.csps.backend.repository;

import org.csps.backend.domain.entities.Merch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface MerchRepository extends JpaRepository<Merch, Long>{
    boolean existsByMerchName(String merchName);
}
