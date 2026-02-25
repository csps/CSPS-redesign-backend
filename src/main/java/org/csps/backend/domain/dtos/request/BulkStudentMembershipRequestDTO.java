package org.csps.backend.domain.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk creating student memberships.
 * Allows admins to enroll multiple students in a single membership year.
 * Duplicates and non-existent students are silently skipped.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkStudentMembershipRequestDTO {

    @NotEmpty(message = "Student IDs cannot be empty")
    private List<String> studentIds;

    @NotNull(message = "Year start is required")
    private Integer yearStart;

    @NotNull(message = "Year end is required")
    private Integer yearEnd;
}
