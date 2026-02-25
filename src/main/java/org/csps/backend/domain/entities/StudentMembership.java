package org.csps.backend.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(indexes={
    @Index(name = "idx_membership_student_id", columnList = "student_id"),
    @Index(name = "idx_membership_active", columnList = "student_id, active"),
    @Index(name = "idx_membership_year", columnList = "year_start, year_end, student_id")
},
uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year_start", "year_end", "student_id"}, name = "uk_membership_year_student")
})
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

    @Column(nullable = false)
    private int yearStart;

    @Column(nullable = false)
    private int yearEnd;

    @PrePersist
    protected void onCreate() {
        this.dateJoined = LocalDateTime.now();
    }
}
