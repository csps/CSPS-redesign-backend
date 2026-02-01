package org.csps.backend.domain.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentWithMembershipsResponseDTO {
    private String studentId;
    private Byte yearLevel;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String role;
    private List<StudentMembershipResponseDTO> memberships;
}