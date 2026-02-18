package org.csps.backend.domain.dtos.request;

import java.time.LocalDate;

import org.csps.backend.domain.enums.AdminPosition;

import lombok.Data;

@Data
public class AdminUnsecureRequestDTO {
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private AdminPosition position;
}