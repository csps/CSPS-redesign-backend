package org.csps.backend.domain.entities;

import org.csps.backend.domain.enums.AdminPosition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
import org.csps.backend.domain.enums.AdminPosition;
=======

>>>>>>> dfd086854119820a393a4fbc00ea6bf2ce226653
@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
<<<<<<< HEAD
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId; // Admin’s own PK

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Same pattern from User to Student, cause previous code did not work.
=======
    private Long userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
>>>>>>> dfd086854119820a393a4fbc00ea6bf2ce226653
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminPosition position;
}
