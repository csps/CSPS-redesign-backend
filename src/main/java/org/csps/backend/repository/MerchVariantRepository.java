package org.csps.backend.repository;

import java.util.List;

import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.enums.ClothingSizing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchVariantRepository extends  JpaRepository<MerchVariant, Long>{
    boolean existsByMerchAndColorAndSize(Merch merch, String color, ClothingSizing size);
    List<MerchVariant> findByMerchMerchId(Long merchId);
}
