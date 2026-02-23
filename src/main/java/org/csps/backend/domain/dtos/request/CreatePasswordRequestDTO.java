package org.csps.backend.domain.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
