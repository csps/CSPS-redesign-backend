package org.csps.backend.domain.dtos.request;

import com.fasterxml.jackson.annotation.JsonAlias;

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

    @NotNull(message = "Year level is required")
    private Byte yearLevel;
    
    @NotNull(message = "User Info is required")
    @JsonAlias("userProfile")
    private UserRequestDTO userRequestDTO;

}

