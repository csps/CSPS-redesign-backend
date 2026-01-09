package org.csps.backend.repository;

import java.util.List;

import org.csps.backend.domain.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
    List<Purchase> findByStudentStudentId(String studentId);

}
