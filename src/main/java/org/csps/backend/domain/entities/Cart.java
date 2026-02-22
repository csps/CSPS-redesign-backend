package org.csps.backend.domain.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "StudentCart")
@NamedEntityGraph(
    name = "Cart.withItemsAndVariants",
    attributeNodes = @NamedAttributeNode(
        value = "items",
        subgraph = "items-with-variant"
    ),
    subgraphs = {
        @NamedSubgraph(
            name = "items-with-variant",
            attributeNodes = @NamedAttributeNode(
                value = "merchVariantItem",
                subgraph = "variant-item-with-merch"
            )
        ),
        @NamedSubgraph(
            name = "variant-item-with-merch",
            attributeNodes = @NamedAttributeNode(
                value = "merchVariant",
                subgraph = "variant-with-merch"
            )
        ),
        @NamedSubgraph(
            name = "variant-with-merch",
            attributeNodes = @NamedAttributeNode("merch")
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    private String cartId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "cartId", referencedColumnName = "studentId")
    private Student student;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items;
}
