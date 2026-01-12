 package org.csps.backend.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.request.MerchUpdateRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.response.MerchDetailedResponseDTO;
import org.csps.backend.domain.dtos.response.MerchSummaryResponseDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.MerchAlreadyExistException;
import org.csps.backend.exception.MerchNotFoundException;
import org.csps.backend.exception.MerchVariantAlreadyExisted;
import org.csps.backend.mapper.MerchMapper;
import org.csps.backend.mapper.MerchVariantMapper;
import org.csps.backend.repository.MerchRepository;
import org.csps.backend.service.MerchService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchServiceImpl implements MerchService {

    private final MerchRepository merchRepository;
    private final MerchMapper merchMapper;

    private final MerchVariantMapper merchVariantMapper;

    @Override
    @Transactional
    public MerchDetailedResponseDTO createMerch(MerchRequestDTO request) {
        if (request == null) throw new InvalidRequestException("Request is required");

        String merchName = request.getMerchName();
        String description = request.getDescription();
        Double merchPrice = request.getPrice();
        MerchType merchType = request.getMerchType();

        // Validate basic merch fields
        if (merchName == null || merchName.trim().isEmpty()) {
            throw new InvalidRequestException("Merchandise name is required");
        }
        if (merchPrice == null || merchPrice < 0) {
            throw new InvalidRequestException("Price is required and must be non-negative");
        }
        if (merchType == null) {
            throw new InvalidRequestException("Merch type is required");
        }

        // Check duplicate name
        if (merchRepository.existsByMerchName(merchName)) {
            throw new MerchAlreadyExistException("Merch Name Already Exist");
        }

        // Map DTO -> entity (basic fields)
        Merch merch = merchMapper.toEntity(request);

        // Validate and map variants carefully
        List<MerchVariantRequestDTO> variantDtos = request.getMerchVariantRequestDto();
        if (variantDtos == null) variantDtos = java.util.List.of();

        Set<String> seenKeys = new HashSet<>();

        List<MerchVariant> merchVariants = variantDtos.stream().map(dto -> {
            if (dto == null) throw new InvalidRequestException("Variant cannot be null");

            Double vPrice = dto.getPrice();
            Integer vStock = dto.getStockQuantity();
            String vColor = dto.getColor();
            String vDesign = dto.getDesign();
            ClothingSizing vSize = dto.getSize();

            if (vPrice == null || vPrice < 0) throw new InvalidRequestException("Variant price is required and must be non-negative");
            if (vStock == null || vStock < 0) throw new InvalidRequestException("Variant stockQuantity is required and must be non-negative");

            switch (merch.getMerchType()) {
                case CLOTHING -> {
                    // clothing requires color and size (design is optional)
                    if (vSize == null) throw new InvalidRequestException("size is required for clothing variant");
                    if (vColor == null || vColor.trim().isEmpty()) throw new InvalidRequestException("color is required for clothing variant");
                    String key = ("CLOTH:" + vColor.trim().toLowerCase() + ":" + vSize.name());
                    if (!seenKeys.add(key)) {
                        throw new MerchVariantAlreadyExisted("Duplicate clothing variant in request: " + vColor + " / " + vSize);
                    }
                }
                case PIN, STICKER, KEYCHAIN -> {
                    // non-clothing requires design; size/color must not be present
                    if (vDesign == null || vDesign.trim().isEmpty()) throw new InvalidRequestException("design is required for this merch type");
                    if (vSize != null) throw new InvalidRequestException("size not allowed for non-clothing variant");
                    if (vColor != null && !vColor.trim().isEmpty()) throw new InvalidRequestException("color not allowed for non-clothing variant");
                    String key = ("DESIGN:" + vDesign.trim().toLowerCase());
                    if (!seenKeys.add(key)) {
                        throw new MerchVariantAlreadyExisted("Duplicate design variant in request: " + vDesign);
                    }
                }
                default -> throw new InvalidRequestException("Unsupported merch type");
            }

            MerchVariant variant = merchVariantMapper.toEntity(dto);
            variant.setMerch(merch);
            return variant;
        }).toList();

        merch.setMerchVariantList(merchVariants);

        // Persist
        Merch saved = merchRepository.save(merch);

        // Build response DTO with variant responses
        MerchDetailedResponseDTO responseDTO = merchMapper.toDetailedResponseDTO(saved);
        List<MerchVariantResponseDTO> variantResponseDTOs = saved.getMerchVariantList()
                .stream()
                .map(merchVariantMapper::toResponseDTO)
                .toList();
        responseDTO.setVariants(variantResponseDTOs);

        return responseDTO;
    }


    
    @Override
    public List<MerchDetailedResponseDTO> getAllMerch() {
        return merchRepository.findAll()
                .stream()
                .map(merchMapper::toDetailedResponseDTO)
                .toList();
    }

    @Override
    public List<MerchSummaryResponseDTO> getAllMerchWithoutVariants() {
        return merchRepository.findAll()
                .stream()
                .map(merchMapper::toSummaryResponseDTO)
                .toList();
    }

    @Override
    public List<MerchSummaryResponseDTO> getMerchByType(MerchType merchType) {
        if (merchType == null) {
            throw new InvalidRequestException("Merch type is required");
        }

        return merchRepository.findByMerchType(merchType)
                .stream()
                .map(merchMapper::toSummaryResponseDTO)
                .toList();
    }

    @Override
    public MerchDetailedResponseDTO getMerchById(Long id) {
        Merch merch = merchRepository.findById(id)
                .orElseThrow(() -> new MerchNotFoundException("Merch not found with id: " + id));
        return merchMapper.toDetailedResponseDTO(merch);
    }

    @Override
    @Transactional
    public MerchDetailedResponseDTO putMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO) {
        // find the merch by id
        Merch foundMerch = merchRepository.findById(merchId)
                            .orElseThrow(() -> new MerchNotFoundException("Merch Not Found"));
        
        // get the new values
        String newMerchName = merchUpdateRequestDTO.getMerchName();
        String newMerchDescription = merchUpdateRequestDTO.getDescription();
        MerchType newMerchType = merchUpdateRequestDTO.getMerchType();
        
        // Validate required fields
        if (newMerchName == null || newMerchName.trim().isEmpty() 
            || newMerchDescription == null || newMerchDescription.trim().isEmpty() 
            || newMerchType == null) {
            throw new InvalidRequestException("Invalid Credential");
        }
    
        // check if the merch name is already exist
        if (!foundMerch.getMerchName().equals(newMerchName) 
            && merchRepository.existsByMerchName(newMerchName)) {
            throw new MerchAlreadyExistException("Merch Name Already Exist");
        }
        
        // set the new values
        foundMerch.setMerchName(newMerchName);
        foundMerch.setMerchType(newMerchType);
        foundMerch.setDescription(newMerchDescription);


        // save the merch
        merchRepository.save(foundMerch);
        
        // return the response
        MerchDetailedResponseDTO merchResponseDTO = merchMapper.toDetailedResponseDTO(foundMerch);
        return merchResponseDTO;
    }

    @Override
    @Transactional
    public MerchDetailedResponseDTO patchMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO) {
        // find the merch by id
        Merch foundMerch = merchRepository.findById(merchId)
                            .orElseThrow(() -> new MerchNotFoundException("Merch Not Found"));
        

        // get the new values
        String newMerchName = merchUpdateRequestDTO.getMerchName();
        String newMerchDescription = merchUpdateRequestDTO.getDescription();
        MerchType newMerchType = merchUpdateRequestDTO.getMerchType();
        

        // set the new values
        if (newMerchName != null && !newMerchName.trim().isEmpty()) {
            foundMerch.setMerchName(newMerchName);
        }
        if (newMerchDescription != null && !newMerchDescription.trim().isEmpty()) {
            foundMerch.setDescription(newMerchDescription);
        }
        if (newMerchType != null && !newMerchType.toString().isEmpty()) {
            foundMerch.setMerchType(newMerchType);
        }


        // save the merch
        merchRepository.save(foundMerch);

        MerchDetailedResponseDTO merchResponseDTO = merchMapper.toDetailedResponseDTO(foundMerch);

        // return the response
        return merchResponseDTO;
    }
}