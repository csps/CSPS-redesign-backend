package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.response.RecoveryTokenResponseDTO;
import org.csps.backend.domain.entities.RecoveryToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecoveryTokenMapper {
    
    @Mapping(target = "userAccountId", source = "userAccount.userAccountId")
    RecoveryTokenResponseDTO toResponseDTO(RecoveryToken entity);
    
    RecoveryToken toEntity(RecoveryTokenResponseDTO dto);
}
