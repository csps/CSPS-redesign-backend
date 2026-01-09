package org.csps.backend.domain.entities;

import org.csps.backend.domain.enums.ClothingSizing;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "merch_variant",
    uniqueConstraints = @UniqueConstraint(columnNames = {"merch_id", "color", "size"}),
    indexes = {
        @Index(name = "idx_merch_id", columnList = "merch_id"),
        @Index(name = "idx_merch_color_size", columnList = "merch_id,color,size"),
        @Index(name = "idx_merch_design", columnList = "merch_id,design")
    }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchVariantId;

    @ManyToOne
    @JoinColumn(name = "merch_id", nullable = false)
    private Merch merch;

    @Column(nullable = true)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ClothingSizing size;

    @Column(nullable = true)
    private String design;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stockQuantity;
}