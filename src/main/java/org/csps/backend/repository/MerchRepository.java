package org.csps.backend.repository;

import java.util.List;

import org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO;
import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.domain.enums.MerchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
@EnableJpaRepositories
public interface MerchRepository extends JpaRepository<Merch, Long>{
    boolean existsByMerchName(String merchName);
    List<Merch> findByMerchType(org.csps.backend.domain.enums.MerchType merchType);

    @Query("SELECT DISTINCT mvi.size FROM MerchVariantItem mvi WHERE mvi.merchVariant.merch.merchId = :merchId AND mvi.stockQuantity > 0")
    List<ClothingSizing> findAvailableClothingSize(@Param("merchId") Long merchId);


    @Query("""
        SELECT new org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO(
            m.merchId, 
            m.merchName, 
            m.description, 
            m.merchType, 
            m.basePrice, 
            m.s3ImageKey, 
            CAST(SUM(i.stockQuantity) AS int)
        )
        FROM Merch m
        LEFT JOIN m.merchVariantList v
        LEFT JOIN v.merchVariantItems i
        GROUP BY m.merchId
    """)
    List<MerchSummaryResponseDTO> findAllSummaries();

    @Query("""
        SELECT new org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO(
            m.merchId, 
            m.merchName, 
            m.description, 
            m.merchType, 
            m.basePrice, 
            m.s3ImageKey, 
            CAST(COALESCE(SUM(i.stockQuantity), 0) AS int)
        )
        FROM Merch m
        LEFT JOIN m.merchVariantList v
        LEFT JOIN v.merchVariantItems i
        WHERE m.merchType = :type
        GROUP BY m.merchId, m.merchName, m.description, m.merchType, m.basePrice, m.s3ImageKey
    """)
    List<MerchSummaryResponseDTO> findAllSummaryByType(@Param("type") MerchType type);
}
