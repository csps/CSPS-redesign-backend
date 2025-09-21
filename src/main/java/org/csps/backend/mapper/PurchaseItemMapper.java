package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.response.PurchaseItemResponseDTO;
import org.csps.backend.domain.entities.PurchaseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseItemMapper {
    @Mapping(source = "purchaseItemId.variantId", target = "merchVariantId")
    @Mapping(source = "purchaseItemId.purchaseId", target = "purchaseId")
    @Mapping(source = "quantity", target = "quantity")
    PurchaseItemResponseDTO toResponseDTO(PurchaseItem purchaseItem);
}
