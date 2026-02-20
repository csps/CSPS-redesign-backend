package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.UserRequestDTO;
import org.csps.backend.domain.dtos.response.UserResponseDTO;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    // Map UserAccount → UserResponseDto
    @Mapping(target = "userId", source = "userProfile.userId")
    @Mapping(target = "firstName", source = "userProfile.firstName")
    @Mapping(target = "lastName", source = "userProfile.lastName")
    @Mapping(target = "middleName", source = "userProfile.middleName")
    @Mapping(target = "birthDate", source = "userProfile.birthDate")
    @Mapping(target = "email", source = "userProfile.email")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "isVerified", source = "isVerified")
    @Mapping(target = "isProfileComplete", source = "userProfile.isProfileComplete")
    UserResponseDTO toResponseDTO(UserAccount userAccount);


    // Mapping from UserRequestDTO to UserAccount (Ignored the Username and Password)
    @Mapping(target = "userAccountId", ignore = true)
    @Mapping(target = "userProfile", ignore = true) // Handle profile separately
    UserAccount toUserAccount(UserRequestDTO dto);

    // Mapping from UserRequestDTO to UserProfile
    @Mapping(target = "userId", ignore = true) // ignored = true, because it's auto-generated
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "middleName", source = "middleName")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "email", source = "email")
    UserProfile toUserProfile(UserRequestDTO dto);


}
