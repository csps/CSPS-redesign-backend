package org.csps.backend.repository;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.enums.ClothingSizing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchVariantRepository extends  JpaRepository<MerchVariant, Long>{
    boolean existsByMerchAndColorAndSize(Merch merch, String color, ClothingSizing size);
    boolean existsByMerchAndDesign(Merch merch, String design);
    boolean existsByMerchAndSize(Merch merch, ClothingSizing size);

    List<MerchVariant> findByMerchMerchId(Long merchId);
    Optional<MerchVariant> findByMerchMerchIdAndSize(Long merchId, ClothingSizing size);
    java.util.List<MerchVariant> findAllByMerchMerchIdAndSize(Long merchId, ClothingSizing size);
    Optional<MerchVariant> findByMerchMerchIdAndColor(Long merchId, String color);
    Optional<MerchVariant> findByMerchMerchIdAndColorAndSize(Long merchId, String color, ClothingSizing size);
    Optional<MerchVariant> findByMerchMerchIdAndDesign(Long merchId, String design);

    boolean existsByMerchMerchIdAndColorAndSize(Long merchId, String color, ClothingSizing size);
    boolean existsByMerchMerchIdAndDesign(Long merchId, String design);

    @Query("SELECT DISTINCT mv.size FROM MerchVariant mv WHERE mv.merch.merchId = :merchId AND mv.color = :color AND mv.stockQuantity > 0")
    List<ClothingSizing> findAvailableSizesByMerchIdAndColor(@Param("merchId") Long merchId, @Param("color") String color);

    @Query("SELECT DISTINCT mv.color FROM MerchVariant mv WHERE mv.merch.merchId = :merchId AND mv.size = :size AND mv.stockQuantity > 0")
    List<String> findAvailableColorsByMerchIdAndSize(@Param("merchId") Long merchId, @Param("size") ClothingSizing size);
}
