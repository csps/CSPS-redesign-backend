package org.csps.backend.domain.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
