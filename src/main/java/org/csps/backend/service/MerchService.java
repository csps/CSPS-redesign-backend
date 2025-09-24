// MerchService.java
package org.csps.backend.service;

import java.util.List;
import java.util.Map;

import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.request.MerchUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.MerchResponseDTO;

public interface MerchService {
    MerchResponseDTO createMerch(MerchRequestDTO request);
    List<MerchResponseDTO> getAllMerch();
    MerchResponseDTO getMerchById(Long id);
    Map<String, Object> putMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO);
    Map<String, Object> patchMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO);
}
