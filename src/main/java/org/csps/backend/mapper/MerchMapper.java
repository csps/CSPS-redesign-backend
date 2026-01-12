package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.response.MerchDetailedResponseDTO;
import org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO;
import org.csps.backend.domain.entities.Merch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MerchVariantMapper.class})
public interface MerchMapper {
    // Entity -> DTO
    @Mapping(target = "variants", source = "merchVariantList")
    MerchDetailedResponseDTO toDetailedResponseDTO (Merch merch);

    

    // DTO -> Entity
    @Mapping(target = "merchVariantList", source = "merchVariantRequestDto")
    Merch toEntity(MerchRequestDTO dto);
}
