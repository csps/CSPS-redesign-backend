package org.csps.backend.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csps.backend.domain.enums.MerchType;

import java.util.List;

@Builder
@Entity
@Table(name = "merch", indexes = {
    @Index(name = "idx_merch_type", columnList = "merch_type")
})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Merch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchId;

    @Column(nullable = false)
    private String merchName;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchType merchType;

    @Column(nullable = false)
    private Double basePrice;

    @Column(nullable = false)
    private String s3ImageKey;  // S3 object key of the first variant's image - REQUIRED

    @OneToMany(mappedBy = "merch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MerchVariant> merchVariantList;
}
