package org.csps.backend.repository;

import java.util.List;

import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.student.studentId = :studentId")
    public List<Order> findByStudentId(String studentId);

    @Query("SELECT o FROM Order o WHERE o.orderStatus = :orderStatus")
    public List<Order> findByOrderStatus(OrderStatus orderStatus);
}
