 package org.csps.backend.service.impl;

import java.io.IOException;
import java.util.List;

import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.request.MerchUpdateRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantItemRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.response.MerchDetailedResponseDTO;
import org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO;
import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.MerchAlreadyExistException;
import org.csps.backend.exception.MerchNotFoundException;
import org.csps.backend.exception.MerchVariantNotFoundException;
import org.csps.backend.mapper.MerchMapper;
import org.csps.backend.repository.MerchRepository;
import org.csps.backend.repository.MerchVariantRepository;
import org.csps.backend.service.MerchService;
import org.csps.backend.service.MerchVariantItemService;
import org.csps.backend.service.MerchVariantService;
import org.csps.backend.service.S3Service;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service implementation for Merch (base merchandise).
 * Manages merch creation, retrieval, and updates.
 * Delegates variant operations to MerchVariantService.
 * Delegates item-level (size/stock) operations to MerchVariantItemService.
 */
@Service
@RequiredArgsConstructor
public class MerchServiceImpl implements MerchService {

    private final MerchRepository merchRepository;
    private final MerchVariantRepository merchVariantRepository;
    private final MerchMapper merchMapper;
    private final S3Service s3Service;
    private final MerchVariantService merchVariantService;
    private final MerchVariantItemService merchVariantItemService;

    @Override
    @Transactional
    public MerchDetailedResponseDTO createMerch(MerchRequestDTO request) throws IOException {
        if (request == null) {
            throw new InvalidRequestException("Request is required");
        }

        String merchName = request.getMerchName();
        MerchType merchType = request.getMerchType();
        String description = request.getDescription();
        Double basePrice = request.getBasePrice();

        // Validate merch fields
        if (merchName == null || merchName.trim().isEmpty()) {
            throw new InvalidRequestException("Merchandise name is required");
        }
        if (merchType == null) {
            throw new InvalidRequestException("Merchandise type is required");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidRequestException("Description is required");
        }

        // Check duplicate name
        if (merchRepository.existsByMerchName(merchName)) {
            throw new MerchAlreadyExistException("Merch name already exists");
        }

        // PHASE 1: Create base Merch entity
        Merch merch = Merch.builder()
                .merchName(merchName)
                .description(description)
                .merchType(merchType)
                .basePrice(basePrice)
                .s3ImageKey("placeholder")
                .build();

        Merch savedMerch = merchRepository.save(merch);

        // Upload merch image if provided
        if (request.getMerchImage() != null && !request.getMerchImage().isEmpty()) {
            String s3ImageKey = s3Service.uploadFile(request.getMerchImage(), savedMerch.getMerchId(), "merch");
            savedMerch.setS3ImageKey(s3ImageKey);
            savedMerch = merchRepository.save(savedMerch);
        }

        // PHASE 2 & 3: Process variants and items
        List<MerchVariantRequestDTO> variants = request.getMerchVariantRequestDto();
        if (variants == null || variants.isEmpty()) {
            throw new InvalidRequestException("At least one variant is required");
        }

        for (MerchVariantRequestDTO variantDto : variants) {
            if(merchType == MerchType.CLOTHING) {
                // For clothing, either color or design must be provided
                if ((variantDto.getColor() == null || variantDto.getColor().trim().isEmpty())
                    && (variantDto.getDesign() == null || variantDto.getDesign().trim().isEmpty())) {
                    throw new InvalidRequestException("Either color or design is required for clothing variants");
                }
            } else {
                // For non-clothing, design is required
                if (variantDto.getDesign() == null || variantDto.getDesign().trim().isEmpty()) {
                    throw new InvalidRequestException("Design is required for non-clothing variants");
                }
            }

            // PHASE 2: Create MerchVariant with color and design
            MerchVariantRequestDTO variantReqDto = MerchVariantRequestDTO.builder()
                    .color(variantDto.getColor())
                    .design(variantDto.getDesign())
                    .variantImage(variantDto.getVariantImage())
                    .build();

            var variantResponse = merchVariantService.addVariantToMerch(savedMerch.getMerchId(), variantReqDto);

            // Upload variant image if provided
            if (variantDto.getVariantImage() != null && !variantDto.getVariantImage().isEmpty()) {
                merchVariantService.uploadVariantImage(variantResponse.getMerchVariantId(), variantDto.getVariantImage());
            }

            // PHASE 3: Create MerchVariantItem(s) for this variant
            List<MerchVariantItemRequestDTO> items = variantDto.getVariantItems();
            if (items == null || items.isEmpty()) {
                throw new InvalidRequestException("At least one item (size/price/stock) is required for each variant");
            }

            // Add multiple items to the variant in batch
            merchVariantItemService.addMultipleItemsToVariant(variantResponse.getMerchVariantId(), items);
        }

        // Reload merch with all variants and items, then build complete response
        Merch finalMerch = merchRepository.findById(savedMerch.getMerchId())
                .orElseThrow(() -> new MerchNotFoundException("Merch not found"));

        return merchMapper.toDetailedResponseDTO(finalMerch);
    }

    @Override
    public List<MerchDetailedResponseDTO> getAllMerch() {
        return merchRepository.findAll().stream()
                .map(merchMapper::toDetailedResponseDTO)
                .toList();
    }

    @Override
    public List<MerchSummaryResponseDTO> getAllMerchSummaries() {
        return merchRepository.findAllSummaries();
    }

    @Override
    public MerchDetailedResponseDTO getMerchById(Long id) {
        Merch merch = merchRepository.findById(id)
                .orElseThrow(() -> new MerchNotFoundException("Merch not found with id: " + id));
        return merchMapper.toDetailedResponseDTO(merch);
    }

    @Override
    public List<MerchSummaryResponseDTO> getMerchByType(MerchType merchType) {
        if (merchType == null) {
            throw new InvalidRequestException("Merch type is required");
        }

        return merchRepository.findAllSummaryByType(merchType);
    }

    @Override
    @Transactional
    public MerchDetailedResponseDTO putMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO) throws IOException {
        // Find merch by ID
        Merch foundMerch = merchRepository.findById(merchId)
                .orElseThrow(() -> new MerchNotFoundException("Merch not found with id: " + merchId));

        // Validate required fields
        if (merchUpdateRequestDTO.getMerchName() == null || merchUpdateRequestDTO.getMerchName().trim().isEmpty()
            || merchUpdateRequestDTO.getDescription() == null || merchUpdateRequestDTO.getDescription().trim().isEmpty()
            || merchUpdateRequestDTO.getMerchType() == null) {
            throw new InvalidRequestException("Invalid credential");
        }

        // Check for duplicate name (excluding current merch)
        if (!foundMerch.getMerchName().equals(merchUpdateRequestDTO.getMerchName())
            && merchRepository.existsByMerchName(merchUpdateRequestDTO.getMerchName())) {
            throw new MerchAlreadyExistException("Merch name already exists");
        }

        // Update fields
        foundMerch.setMerchName(merchUpdateRequestDTO.getMerchName());
        foundMerch.setDescription(merchUpdateRequestDTO.getDescription());
        foundMerch.setMerchType(merchUpdateRequestDTO.getMerchType());

        Merch updated = merchRepository.save(foundMerch);
        return merchMapper.toDetailedResponseDTO(updated);
    }

    @Override
    @Transactional
    public MerchDetailedResponseDTO patchMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO) throws IOException {
        // Find merch by ID
        Merch foundMerch = merchRepository.findById(merchId)
                .orElseThrow(() -> new MerchNotFoundException("Merch not found with id: " + merchId));

        // Partial updates
        if (merchUpdateRequestDTO.getMerchName() != null && !merchUpdateRequestDTO.getMerchName().trim().isEmpty()) {
            if (!foundMerch.getMerchName().equals(merchUpdateRequestDTO.getMerchName())
                && merchRepository.existsByMerchName(merchUpdateRequestDTO.getMerchName())) {
                throw new MerchAlreadyExistException("Merch name already exists");
            }
            foundMerch.setMerchName(merchUpdateRequestDTO.getMerchName());
        }
        if (merchUpdateRequestDTO.getDescription() != null && !merchUpdateRequestDTO.getDescription().trim().isEmpty()) {
            foundMerch.setDescription(merchUpdateRequestDTO.getDescription());
        }
        if (merchUpdateRequestDTO.getMerchType() != null) {
            foundMerch.setMerchType(merchUpdateRequestDTO.getMerchType());
        }

        Merch updated = merchRepository.save(foundMerch);
        return merchMapper.toDetailedResponseDTO(updated);    
    }
}