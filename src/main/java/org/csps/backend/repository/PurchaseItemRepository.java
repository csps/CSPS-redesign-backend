package org.csps.backend.repository;

import org.csps.backend.domain.entities.PurchaseItem;
import org.csps.backend.domain.entities.composites.PurchaseItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, PurchaseItemId>{

}
