package org.csps.backend.domain.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentMembershipRequestDTO {

    @NotBlank(message = "Student ID is required")
    @Size(min = 8, max = 8, message = "Invalid Student ID format")
    private String studentId;

    @NotNull(message = "Active status is required")
    private boolean active;

    @NotNull(message = "Academic year is required")
    @Min(1)
    @Max(4)
    private Byte academicYear;

    @NotNull(message = "Semester is required")
    @Min(1)
    @Max(2)
    private Byte semester;
}
