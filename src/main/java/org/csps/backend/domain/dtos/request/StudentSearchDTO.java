package org.csps.backend.domain.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentSearchDTO {
    
    private String studentName;
    
    private String studentId;
    
    private Byte yearLevel;
}
