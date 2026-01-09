package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.response.PurchaseItemResponseDTO;
import org.csps.backend.domain.entities.PurchaseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses={MerchVariantMapper.class})
public interface PurchaseItemMapper {


    @Mapping(source = "purchaseItemId.purchaseId", target = "purchaseId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "merchVariant.merch.merchName", target = "merchName")
    @Mapping(source = "merchVariant.merch.merchType", target = "merchType")
    PurchaseItemResponseDTO toResponseDTO(PurchaseItem purchaseItem);

    @Mapping(target="merchVariant", source="merchVariant")
    @Mapping(target="purchaseItemId.purchaseId", source="purchaseId")
    PurchaseItem toEntity(PurchaseItemResponseDTO purchaseItemResponseDTO);


    
}
