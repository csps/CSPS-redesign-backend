package org.csps.backend.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.csps.backend.domain.entities.composites.PurchaseItemId;
import org.csps.backend.domain.enums.PurchaseItemStatus;

@Entity
@Table(name = "purchase_items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItem {

    @EmbeddedId
    private PurchaseItemId purchaseItemId;

    @ManyToOne
    @MapsId("purchaseId") // maps purchaseItemId.purchaseId
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @ManyToOne
    @MapsId("variantId") // maps purchaseItemId.variantId
    @JoinColumn(name = "merch_variant_id", nullable = false)
    private MerchVariant merchVariant;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseItemStatus status;

    
}
