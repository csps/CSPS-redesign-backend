package org.csps.backend.domain.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailRequestDTO {

    @NotBlank(message = "New email cannot be blank")
    @Email(message = "Invalid email format")
    private String newEmail;

    @NotBlank(message = "Verification code cannot be blank")
    private String verificationCode;
}
