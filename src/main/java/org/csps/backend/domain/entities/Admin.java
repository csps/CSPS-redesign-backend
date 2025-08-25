package org.csps.backend.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csps.backend.domain.enums.AdminPosition;

@Entity
@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Admin {

    @Id
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id")
    @MapsId
    private User user;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminPosition position;

}