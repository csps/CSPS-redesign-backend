package org.csps.backend.domain.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search filter DTO for querying student memberships with JPA Specification.
 * All fields are optional — null fields are ignored in the predicate build.
 *
 * @field studentName  partial match on first or last name (case-insensitive)
 * @field studentId    exact match on 8-character student ID
 * @field activeStatus "ACTIVE", "INACTIVE", or null for all
 * @field yearStart    filter by membership academic year start
 * @field yearEnd      filter by membership academic year end
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentMembershipSearchDTO {

    private String studentName;

    private String studentId;

    private String activeStatus;

    private Integer yearStart;

    private Integer yearEnd;
}
