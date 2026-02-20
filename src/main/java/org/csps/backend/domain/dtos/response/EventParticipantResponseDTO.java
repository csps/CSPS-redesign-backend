package org.csps.backend.domain.dtos.response;

import java.time.LocalDateTime;

import org.csps.backend.domain.enums.ParticipationStatus;

import lombok.Data;

@Data
public class EventParticipantResponseDTO {
    private Long participantId;
    private String studentId;
    private String studentName;
    private ParticipationStatus participationStatus;
    private LocalDateTime joinedDate;
}
