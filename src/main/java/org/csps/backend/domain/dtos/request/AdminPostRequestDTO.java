package org.csps.backend.domain.dtos.request;

import org.csps.backend.domain.enums.AdminPosition;

import lombok.Data;

@Data
public class AdminPostRequestDTO {
    private AdminPosition position;

    private String studentId;
}
