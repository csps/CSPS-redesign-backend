package org.csps.backend.domain.dtos.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceRecordSearchDTO {
    
    private String studentId;
    
    private String studentName;
    
    private Long sessionId;
    
    private String sessionName;
    
    private Long eventId;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private String sortBy;
    
    private String sortDirection;
}
