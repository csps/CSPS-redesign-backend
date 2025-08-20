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
@Table
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

    @OneToMany(mappedBy = "merch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MerchVariant> merchVariantList;

}
