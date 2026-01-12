package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.request.MerchVariantItemRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.response.MerchVariantItemResponseDTO;
import org.csps.backend.domain.entities.MerchVariantItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses={MerchVariantMapper.class})
public interface MerchVariantItemMapper {

    // Map request DTO -> entity
    
    MerchVariantItem toEntity(MerchVariantItemRequestDTO dto);

    // Map entity -> response DTO (include parent variant and merch info)
    @Mapping(target = "merchVariantItemId", source = "merchVariantItemId")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "stockQuantity", source = "stockQuantity")
    @Mapping(target = "price", source = "price")
    MerchVariantItemResponseDTO toResponseDto(MerchVariantItem item);

}
