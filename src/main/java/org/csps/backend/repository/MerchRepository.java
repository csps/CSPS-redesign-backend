package org.csps.backend.repository;

import java.util.List;

import org.csps.backend.domain.entities.Merch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Service
public interface MerchRepository extends JpaRepository<Merch, Long>{
    boolean existsByMerchName(String merchName);
    List<Merch> findByMerchType(org.csps.backend.domain.enums.MerchType merchType);
}
