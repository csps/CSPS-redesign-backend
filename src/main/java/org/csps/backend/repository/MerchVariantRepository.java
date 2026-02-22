package org.csps.backend.repository;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.enums.ClothingSizing;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface MerchVariantRepository extends  JpaRepository<MerchVariant, Long>{
    
    @EntityGraph(value = "MerchVariant.withItems", type = EntityGraph.EntityGraphType.FETCH)
    List<MerchVariant> findByMerchMerchId(Long merchId);

    Optional<MerchVariant> findByMerchMerchIdAndColor(Long merchId, String color);
    Optional<MerchVariant> findByMerchMerchIdAndDesign(Long merchId, String design);

    boolean existsByMerchMerchIdAndColor(Long merchId, String color);
    boolean existsByMerchMerchIdAndDesign(Long merchId, String design);

}
