package org.csps.backend.domain.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckInRequestDTO {
    @NotBlank(message = "QR token cannot be blank")
    private String qrToken;
}
