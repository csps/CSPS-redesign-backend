package org.csps.backend.domain.dtos.response;


import org.csps.backend.domain.enums.AdminPosition;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDTO {
    private String studentId;
    private Byte yearLevel;

    @JsonAlias("user_profile")
    private UserResponseDTO user;
    
    
    private AdminPosition adminPosition;
}
