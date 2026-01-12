package org.csps.backend.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.csps.backend.domain.enums.OrderStatus;

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

    @ManyToOne
    @JoinColumn(name = "merch_variant_item_id", nullable = false)
    private MerchVariantItem orderItems;

    @Column(nullable = false)
    private LocalDate orderDate;

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
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
