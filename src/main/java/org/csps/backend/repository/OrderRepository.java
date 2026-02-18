package org.csps.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    
    @Query("SELECT o FROM Order o WHERE o.student.studentId = :studentId")
    List<Order> findByStudentId(String studentId);
    
    @Query("SELECT o FROM Order o WHERE o.student.studentId = :studentId ORDER BY o.orderDate DESC")
    Page<Order> findByStudentId(String studentId, Pageable pageable);

    

    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
    
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Order> findByOrderDateBetweenAndOrderStatus(LocalDateTime start, LocalDateTime end, OrderStatus status);
    
    List<Order> findByOrderStatus(OrderStatus status);
}

    