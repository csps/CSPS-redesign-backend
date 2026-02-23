package org.csps.backend.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csps.backend.domain.entities.composites.CartItemId;


@Entity
@Table(name = "cart_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @EmbeddedId
    private CartItemId id;

    @ManyToOne
    @MapsId("cartId")   // FK + PK
    @JoinColumn(name = "student_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @MapsId("merchVariantItemId") // Change this to the Item (SKU)
    @JoinColumn(name = "merch_variant_item_id", nullable = false)
    private MerchVariantItem merchVariantItem;

    private int quantity;
}

