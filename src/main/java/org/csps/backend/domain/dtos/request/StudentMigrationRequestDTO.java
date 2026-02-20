package org.csps.backend.domain.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* minimal student data for bulk migration from CSV */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentMigrationRequestDTO {

    @NotBlank(message = "Student ID is required")
    @Size(min = 8, max = 8, message = "Invalid Student ID format")
    private String studentId;

    @NotNull(message = "Year level is required")
    private Byte yearLevel;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
}
