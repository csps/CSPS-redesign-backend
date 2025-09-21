package org.csps.backend.mapper;

import org.csps.backend.domain.dtos.response.PurchaseResponseDTO;
import org.csps.backend.domain.entities.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PurchaseItemMapper.class})
public interface PurchaseMapper {
    @Mapping(target = "studentId", source = "student.studentId")
    @Mapping(target = "items", source = "items")
    PurchaseResponseDTO toResponseDTO(Purchase purchase);
}
