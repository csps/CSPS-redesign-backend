package org.csps.backend.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long membershipId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable=false)
    private LocalDateTime dateJoined;

    @Column(nullable = false)
    private boolean active;

    @Min(1)
    @Max(4)
    @Column(nullable = false)
    private byte academicYear;

    @Min(1)
    @Max(2)
    @Column(nullable = false)
    private byte semester;

    @PrePersist
    protected void onCreate() {
        this.dateJoined = LocalDateTime.now();
    }
}
