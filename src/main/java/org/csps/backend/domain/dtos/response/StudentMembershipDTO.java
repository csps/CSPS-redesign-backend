package org.csps.backend.domain.dtos.response;

import lombok.Data;

@Data
public class StudentMembershipDTO {
    private String studentId;
    private String fullName;
    private String idNumber;
    private Boolean isPaid; // Maps to "Paid" / "Not Paid"
}