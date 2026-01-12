// MerchService.java
package org.csps.backend.service;

import java.io.IOException;
import java.util.List;

import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.request.MerchUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.MerchDetailedResponseDTO;
import org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO;
import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.domain.enums.MerchType;

/**
 * Service for managing Merch (base merchandise).
 * Variant-level operations are delegated to MerchVariantService.
 * Item-level (size/stock) operations are delegated to MerchVariantItemService.
 */
public interface MerchService {
    
    
    /**
     * Create complete merch with all variants and items in one operation.
     * Phase 1: Create Merch (name, image, description, merchType)
     * Phase 2: Create MerchVariant(s) with color, design, image
     * Phase 3: Create MerchVariantItem(s) for each variant (size, price, stock)
     */
    MerchDetailedResponseDTO createMerch(MerchRequestDTO request) throws IOException;
    
    /**
     * Get all merch with full details including variants.
     */
    List<MerchDetailedResponseDTO> getAllMerch();
    
    /**
     * Get all merch summaries (without variants to reduce payload).
     */
    List<MerchSummaryResponseDTO> getAllMerchSummaries();
    
    /**
     * Get all merch summaries (alias for getAllMerchSummaries for backward compatibility).
     */
    default List<MerchSummaryResponseDTO> getAllMerchWithoutVariants() {
        return getAllMerchSummaries();
    }
    
    /**
     * Get single merch by ID with full details.
     */
    MerchDetailedResponseDTO getMerchById(Long id);
    
    /**
     * Get merch by type.
     */
    List<MerchSummaryResponseDTO> getMerchByType(MerchType merchType);
    
    /**
     * Update merch (full update).
     */
    MerchDetailedResponseDTO putMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO) throws IOException;
    
    /**
     * Partial update of merch.
     */
    MerchDetailedResponseDTO patchMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO) throws IOException;
    
}
