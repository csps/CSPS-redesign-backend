package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.ClothingResponseDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.enums.ClothingSizing;

public interface MerchVariantService {
    MerchVariantResponseDTO addMerchVariant(MerchVariantRequestDTO dto);
    MerchVariantResponseDTO addVariantToMerch(Long merchId, MerchVariantRequestDTO dto);
    List<MerchVariantResponseDTO> addAllMerchVariant (List<MerchVariantRequestDTO> merchVariantRequests);
    List<MerchVariantResponseDTO> getAllMerchVariant();
    MerchVariantResponseDTO getMerchVariantBySize(ClothingSizing size, Long merchId);
    MerchVariantResponseDTO getMerchVariant(Long merchId, String color, ClothingSizing size, String design);
    List<MerchVariantResponseDTO> getMerchVariantByMerchId(Long merchId);
    List<ClothingSizing> getAvailableSizesForColor(Long merchId, String color);
    ClothingResponseDTO getClothingBySize(Long merchId, ClothingSizing size);
    MerchVariantResponseDTO putMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO);
    MerchVariantResponseDTO patchMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO);
}
