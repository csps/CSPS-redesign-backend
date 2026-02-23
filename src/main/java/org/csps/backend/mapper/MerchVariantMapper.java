package org.csps.backend.mapper;

import java.util.List;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.entities.MerchVariantItem;
import org.csps.backend.domain.enums.ClothingSizing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses={MerchVariantItemMapper.class})
public interface MerchVariantMapper {

    // Entity -> DTO

    @Mapping(target = "price", expression = "java(getFirstPrice(merchVariant))")
    @Mapping(target = "stockQuantity", expression = "java(getFirstStock(merchVariant))")
    @Mapping(target="variantItems", source="merchVariantItems")
    MerchVariantResponseDTO toResponseDTO(MerchVariant merchVariant);

    // DTO -> Entity (service layer typically handles creating MerchVariantItem list)
    @Mapping(target = "merch", ignore = true)
    @Mapping(target = "merchVariantId", ignore = true)
    @Mapping(target = "merchVariantItems", ignore = true)
    MerchVariant toEntity(MerchVariantRequestDTO merchVariantResponseDTO);

    // helpers used in expressions
    default ClothingSizing getFirstSize(MerchVariant merchVariant) {
        List<MerchVariantItem> items = merchVariant == null ? null : merchVariant.getMerchVariantItems();
        if (items == null || items.isEmpty()) return null;
        return items.get(0).getSize();
    }

    default Double getFirstPrice(MerchVariant merchVariant) {
        List<MerchVariantItem> items = merchVariant == null ? null : merchVariant.getMerchVariantItems();
        if (items == null || items.isEmpty()) return null;
        return items.get(0).getPrice();
    }

    default Integer getFirstStock(MerchVariant merchVariant) {
        List<MerchVariantItem> items = merchVariant == null ? null : merchVariant.getMerchVariantItems();
        if (items == null || items.isEmpty()) return null;
        return items.get(0).getStockQuantity();
    }

}
