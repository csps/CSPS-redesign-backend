package org.csps.backend.service;

import java.io.IOException;
import java.util.List;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.enums.ClothingSizing;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for managing MerchVariant (color/design variants of merch).
 * Item-level operations (size/stock) are delegated to MerchVariantItemService.
 */
public interface MerchVariantService {
    
    /**
     * Add a new variant to existing merch.
     * For clothing: requires color, optional design
     * For non-clothing: requires design, no color
     */
    MerchVariantResponseDTO addVariantToMerch(Long merchId, MerchVariantRequestDTO dto) throws IOException;
    
    /**
     * Get all variants.
     */
    List<MerchVariantResponseDTO> getAllMerchVariants();
    
    /**
     * Get all variants for a specific merch.
     */
    List<MerchVariantResponseDTO> getVariantsByMerchId(Long merchId);
    
    /**
     * Get a specific variant by merch, color, and/or design.
     */
    MerchVariantResponseDTO getVariantByMerchAndKey(Long merchId, String color, String design);
    
    /**
     * Upload/update image for a variant.
     */
    String uploadVariantImage(Long merchVariantId, MultipartFile file) throws IOException;

    /**
     * Get available sizes for a merchandise variant (clothing only).
     * Returns sizes with stock quantity > 0.
     */
    List<ClothingSizing> getAvailableSizesForVariant(Long merchVariantId);

    /**
     * Get available sizes with stock quantities for a merchandise variant (clothing only).
     * Returns sizes and their respective stock quantities where stock > 0.
     */

  
}
