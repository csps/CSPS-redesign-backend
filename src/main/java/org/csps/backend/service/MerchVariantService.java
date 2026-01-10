package org.csps.backend.service;

import java.io.IOException;
import java.util.List;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MerchVariantService {
    MerchVariantResponseDTO addMerchVariant(MerchVariantRequestDTO dto);
    MerchVariantResponseDTO addMerchVariantWithImage(MerchVariantRequestDTO dto, MultipartFile imageFile) throws IOException;
    MerchVariantResponseDTO addVariantToMerch(Long merchId, MerchVariantRequestDTO dto);
    List<MerchVariantResponseDTO> addAllMerchVariant (List<MerchVariantRequestDTO> merchVariantRequests);
    List<MerchVariantResponseDTO> getAllMerchVariant();
    List<MerchVariantResponseDTO> getMerchVariantByMerchId(Long merchId);
    MerchVariantResponseDTO putMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO);
    MerchVariantResponseDTO patchMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO);
    String uploadVariantImage(Long merchVariantId, MultipartFile file) throws IOException;
}
