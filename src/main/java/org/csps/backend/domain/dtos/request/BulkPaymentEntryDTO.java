package org.csps.backend.domain.dtos.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single student entry in a bulk payment request.
 * Each entry pairs a student with the actual date they paid,
 * preserving the real purchase timestamp instead of using server time.
 *
 * @field studentId the 8-character student ID who paid
 * @field orderDate the actual date/time the payment was made
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkPaymentEntryDTO {

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;
}
