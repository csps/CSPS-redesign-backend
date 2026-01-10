package org.csps.backend.domain.entities;

import org.csps.backend.domain.enums.ClothingSizing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
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

    // Variant-specific attributes
    private String color;

    @Enumerated(EnumType.STRING)
    private ClothingSizing size;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stockQuantity;


    @Column(nullable = true)
    private String s3ImageKey;  // S3 object key - URL constructed as {S3_BASE_URL}/{s3ImageKey}
}
