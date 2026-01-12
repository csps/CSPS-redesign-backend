package org.csps.backend.repository;

import java.util.List;

import org.csps.backend.domain.entities.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderOrderId(Long orderId);
    
    Page<OrderItem> findByOrderOrderId(Long orderId, Pageable pageable);
}
