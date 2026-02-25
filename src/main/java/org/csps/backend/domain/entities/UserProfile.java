package org.csps.backend.domain.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes={
    @Index(name = "idx_email", columnList = "email", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_profile_id")
    private Long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String middleName;

    @Column(nullable = true)
    private LocalDate birthDate;

    @Column(nullable = true, unique = true)
    @Email(message = "Invalid email format")
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isProfileComplete = false;

    // One-to-many relationship: One UserProfile can have multiple UserAccounts
    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY)
    private List<UserAccount> userAccounts;
}
