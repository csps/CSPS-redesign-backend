package org.csps.backend.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    @Size(min = 8, max = 8, message = "Invalid Student ID format")
    private String studentId;

    @Column(nullable = false)
    private Byte yearLevel;


    @OneToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;
}
