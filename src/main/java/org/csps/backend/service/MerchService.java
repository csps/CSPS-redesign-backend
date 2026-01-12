// MerchService.java
package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.request.MerchUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.MerchDetailedResponseDTO;
import org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO;
import org.csps.backend.domain.enums.MerchType;

public interface MerchService {
    MerchDetailedResponseDTO createMerch(MerchRequestDTO request);
    List<MerchDetailedResponseDTO> getAllMerch();
    List<MerchSummaryResponseDTO> getAllMerchWithoutVariants();
    MerchDetailedResponseDTO getMerchById(Long id);
    List<MerchSummaryResponseDTO> getMerchByType(MerchType merchType);
    MerchDetailedResponseDTO putMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO);
    MerchDetailedResponseDTO patchMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO);
}
