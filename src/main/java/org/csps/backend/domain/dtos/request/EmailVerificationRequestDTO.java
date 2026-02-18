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
public class EmailVerificationRequestDTO {
    
    @NotBlank(message = "verification code is required")
    @Size(min = 6, max = 6, message = "verification code must be 6 digits")
    private String code;
}
