package org.csps.backend.domain.dtos.response;

import lombok.Data;

@Data
public class MembershipRatioDTO {
    private Integer totalStudents;
    private Integer paidMembersCount;
    private Integer nonMembersCount;
    private Double memberPercentage; // Optional, can be calc on frontend
}