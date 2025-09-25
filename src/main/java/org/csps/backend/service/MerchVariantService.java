package org.csps.backend.service;

import java.util.List;
import java.util.Map;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;

public interface MerchVariantService {
    MerchVariantResponseDTO addMerchVariant(MerchVariantRequestDTO dto);
    MerchVariantResponseDTO addVariantToMerch(Long merchId, MerchVariantRequestDTO dto);
    List<MerchVariantResponseDTO> addAllMerchVariant (List<MerchVariantRequestDTO> merchVariantRequests);
    List<MerchVariantResponseDTO> getAllMerchVariant();
    List<MerchVariantResponseDTO> getMerchVariantByMerchId(Long merchId);
    Map<String, Object> putMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO);
    Map<String, Object> patchMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO);
}
