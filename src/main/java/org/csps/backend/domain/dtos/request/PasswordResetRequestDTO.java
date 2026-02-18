package org.csps.backend.domain.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetRequestDTO {
    
    @NotBlank(message = "recovery token is required")
    private String token;
    
    @NotBlank(message = "new password is required")
    @Size(min = 8, message = "password must be at least 8 characters")
    private String newPassword;
    
    @NotBlank(message = "confirm password is required")
    private String confirmPassword;
}
