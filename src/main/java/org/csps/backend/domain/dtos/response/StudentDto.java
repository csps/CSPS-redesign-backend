package org.csps.backend.domain.dtos.response;

import lombok.Data;

@Data
public class StudentDto {
    private Long studentId;
    private byte yearLevel;
    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
}
