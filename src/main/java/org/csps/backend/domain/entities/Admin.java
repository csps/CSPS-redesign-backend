package org.csps.backend.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csps.backend.domain.enums.AdminPosition;
@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId; // Admin’s own PK

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Same pattern from User to Student, cause previous code did not work.
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminPosition position;
}
