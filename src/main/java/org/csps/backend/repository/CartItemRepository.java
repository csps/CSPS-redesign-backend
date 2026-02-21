package org.csps.backend.repository;

import org.csps.backend.domain.entities.CartItem;
import org.csps.backend.domain.entities.composites.CartItemId;
import org.csps.backend.domain.enums.MerchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId>{
    
    @Query("SELECT COUNT(ci) > 0 FROM CartItem ci " +
           "JOIN ci.merchVariantItem mvi " +
           "JOIN mvi.merchVariant mv " +
           "JOIN mv.merch m " +
           "WHERE ci.cart.student.studentId = :studentId AND m.merchType = :merchType")
    boolean existsByStudentIdAndMerchType(@Param("studentId") String studentId, @Param("merchType") MerchType merchType);
}
