package org.csps.backend.repository;

import java.util.List;

import org.csps.backend.domain.entities.OrderItem;
import org.csps.backend.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    @EntityGraph(attributePaths = {"order", "order.student", "order.student.userAccount", "order.student.userAccount.userProfile", "merchVariantItem", "merchVariantItem.merchVariant", "merchVariantItem.merchVariant.merch"}, type = EntityGraph.EntityGraphType.FETCH)
    List<OrderItem> findByOrderOrderId(Long orderId);
    
    @EntityGraph(attributePaths = {"order", "order.student", "order.student.userAccount", "order.student.userAccount.userProfile", "merchVariantItem", "merchVariantItem.merchVariant", "merchVariantItem.merchVariant.merch"}, type = EntityGraph.EntityGraphType.FETCH)
    Page<OrderItem> findByOrderOrderId(Long orderId, Pageable pageable);

    @EntityGraph(attributePaths = {"order", "order.student", "order.student.userAccount", "order.student.userAccount.userProfile", "merchVariantItem", "merchVariantItem.merchVariant", "merchVariantItem.merchVariant.merch"}, type = EntityGraph.EntityGraphType.FETCH)
    Page<OrderItem> findByOrderStatusAndOrderStudentStudentId(OrderStatus status, String studentId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"order", "order.student", "order.student.userAccount", "order.student.userAccount.userProfile", "merchVariantItem", "merchVariantItem.merchVariant", "merchVariantItem.merchVariant.merch"}, type = EntityGraph.EntityGraphType.FETCH)
    Page<OrderItem> findByOrderStudentStudentIdOrderByUpdatedAtDesc(String studentId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"order", "order.student", "order.student.userAccount", "order.student.userAccount.userProfile", "merchVariantItem", "merchVariantItem.merchVariant", "merchVariantItem.merchVariant.merch"}, type = EntityGraph.EntityGraphType.FETCH)
    Page<OrderItem> findByOrderStudentStudentIdAndOrderStatusOrderByUpdatedAtDesc(String studentId, OrderStatus status, Pageable pageable);
    
    /* eager load related entities to prevent N+1 queries in dashboard */
    @EntityGraph(attributePaths = {"order", "order.student", "order.student.userAccount", "order.student.userAccount.userProfile", "merchVariantItem", "merchVariantItem.merchVariant", "merchVariantItem.merchVariant.merch"}, type = EntityGraph.EntityGraphType.FETCH)
    List<OrderItem> findTop5ByOrderStatusInOrderByCreatedAtDesc(List<OrderStatus> statuses);
    
    /* eagerly load order item with student profile and merch details for notifications */
    @EntityGraph(attributePaths = {"order", "order.student", "order.student.userAccount", "order.student.userAccount.userProfile", "merchVariantItem", "merchVariantItem.merchVariant", "merchVariantItem.merchVariant.merch"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderItemId = :id")
    java.util.Optional<OrderItem> findByIdWithStudentAndMerchDetails(@Param("id") Long id);

    /* check if any order items reference merch variant items */
    boolean existsByMerchVariantItemMerchVariantMerchVariantId(Long merchVariantId);
    
    /* check if any order items reference a specific merch */
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.merchVariantItem.merchVariant.merch.merchId = :merchId")
    boolean existsByMerch(@Param("merchId") Long merchId);
}