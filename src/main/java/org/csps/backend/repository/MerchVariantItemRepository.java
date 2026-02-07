package org.csps.backend.repository;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.entities.MerchVariantItem;
import org.csps.backend.domain.enums.ClothingSizing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchVariantItemRepository extends JpaRepository<MerchVariantItem, Long> {
    
    /**
     * Find all items for a specific variant.
     */
    List<MerchVariantItem> findByMerchVariantMerchVariantId(Long merchVariantId);
    
    /**
     * Find item by variant and size.
     */
    Optional<MerchVariantItem> findByMerchVariantAndSize(MerchVariant merchVariant, ClothingSizing size);
    
    /**
     * Check if item with size exists for variant.
     */
    boolean existsByMerchVariantAndSize(MerchVariant merchVariant, ClothingSizing size);
    
    /**
     * Find all items for a variant (alternative query).
     */
    List<MerchVariantItem> findByMerchVariant(MerchVariant merchVariant);
    
    /**
     * Find top 5 items ordered by stock ascending.
     */
    List<MerchVariantItem> findTop5ByOrderByStockQuantityAsc();
}