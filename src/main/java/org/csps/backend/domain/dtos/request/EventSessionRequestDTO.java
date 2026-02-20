package org.csps.backend.domain.dtos.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventSessionRequestDTO {
    @NotBlank(message = "Session name cannot be blank")
    private String sessionName;

    @NotNull(message = "Session date cannot be null")
    private LocalDate sessionDate;

    @NotNull(message = "Start time cannot be null")
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    private LocalTime endTime;
}
