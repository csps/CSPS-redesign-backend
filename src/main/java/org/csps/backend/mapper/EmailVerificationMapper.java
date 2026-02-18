package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.response.EmailVerificationResponseDTO;
import org.csps.backend.domain.entities.EmailVerification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailVerificationMapper {
    
    @Mapping(target = "userAccountId", source = "userAccount.userAccountId")
    EmailVerificationResponseDTO toResponseDTO(EmailVerification entity);
    
    EmailVerification toEntity(EmailVerificationResponseDTO dto);
}
