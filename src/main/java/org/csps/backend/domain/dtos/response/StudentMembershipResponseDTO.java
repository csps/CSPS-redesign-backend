package org.csps.backend.domain.dtos.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentMembershipResponseDTO {
    private Long membershipId;
    private String studentId;

    private LocalDateTime dateJoined;

    private boolean active;

    @Min(1)
    @Max(4)
    @JsonAlias("academic_year")
    private byte academicYear;

    @Min(1)
    @Max(2)
    @JsonAlias("semester")
    private byte semester;
}