package org.csps.backend.domain.dtos.response;

import java.time.LocalDate;
import java.time.LocalTime;

import org.csps.backend.domain.enums.SessionStatus;

import lombok.Data;

@Data
public class EventSessionResponseDTO {
    private Long sessionId;
    private String sessionName;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private SessionStatus sessionStatus;
    private String qrTokenCode;
}
