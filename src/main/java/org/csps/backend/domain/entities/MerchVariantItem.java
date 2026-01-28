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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes= {
    @Index(name="idx_merch_variant_id", columnList="merch_variant_id"),
    @Index(name="idx_size", columnList="size")
})
public class MerchVariantItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long merchVariantItemId;

    @ManyToOne
    @JoinColumn(name = "merch_variant_id", nullable = false)
    private MerchVariant merchVariant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ClothingSizing size;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Double price;

}
