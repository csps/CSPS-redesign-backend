package org.csps.backend.domain.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentRequestDTO {

    @NotBlank(message = "Student ID is required")
    @Size(min = 8, max = 8, message = "Invalid Student ID format")
    private String studentId;

    @NotBlank(message = "Student username is required")
    private String username;

    @NotBlank(message = "Student password is required")
    private String password;

    @NotNull(message = "Year level is required")
    private Byte yearLevel;


    // TODO consider using user ID
    @NotNull(message = "User is required")
    private UserRequestDTO userRequestDTO;

}

