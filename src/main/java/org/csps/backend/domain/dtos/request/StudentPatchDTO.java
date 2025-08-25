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
public class StudentPatchDTO {
    @Size(min = 8, max = 8, message = "Invalid Student ID format")
    private String studentId;

    private String username;

    private String password;

    private Byte yearLevel;

    private UserPatchDTO userPatchDTO;
}
