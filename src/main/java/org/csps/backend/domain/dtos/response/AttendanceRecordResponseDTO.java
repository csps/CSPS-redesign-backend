package org.csps.backend.domain.dtos.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AttendanceRecordResponseDTO {
    private Long attendanceId;
    private Long participantId;
    private String studentId;
    private String studentName;
    private Long sessionId;
    private String sessionName;
    private LocalDateTime checkedInAt;
}
