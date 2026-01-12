package org.csps.backend.domain.entities.composites;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PurchaseItemId implements Serializable {
    private Long purchaseId;
    private Long merchVariantItemId;
}
