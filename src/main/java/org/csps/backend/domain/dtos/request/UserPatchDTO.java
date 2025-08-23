package org.csps.backend.domain.dtos.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csps.backend.domain.enums.UserRole;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPatchDTO {
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    private String middleName;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // TODO admin are only allow to change role
    private UserRole role;
}
