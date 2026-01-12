package org.csps.backend.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.enums.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "orders", indexes = 
{
    @Index(name = "idx_order_status", columnList = "order_status"), 
    @Index(name = "idx_student_id", columnList = "student_id")
}
)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    private Double totalPrice;
    
    @Column(nullable = false)
    private int quantity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
