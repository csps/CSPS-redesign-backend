package org.csps.backend.domain.dtos.response;

import java.time.LocalDate;

import org.csps.backend.domain.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long userId;          // from profile
    private String username;      // from account
    private String firstName;     // from profile
    private String lastName;      // from profile
    private String middleName;    // from profile
    private LocalDate birthDate;       // from profile
    private String email;         // from profile
    private UserRole role;          // from account
}
