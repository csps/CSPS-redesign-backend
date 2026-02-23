package org.csps.backend.domain.dtos.response;


import org.csps.backend.domain.enums.AdminPosition;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminResponseDTO {
    private Long adminId;
    private AdminPosition position;


    private UserResponseDTO user;
}
