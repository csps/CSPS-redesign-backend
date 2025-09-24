 package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.csps.backend.domain.dtos.request.InvalidRequestException;
import org.csps.backend.domain.dtos.request.MerchAlreadyExistException;
import org.csps.backend.domain.dtos.request.MerchRequestDTO;
import org.csps.backend.domain.dtos.request.MerchUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.MerchResponseDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.exception.MerchNotFoundException;
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
    public MerchResponseDTO createMerch(MerchRequestDTO request) {
        // Step 1: Convert request → entity
        Merch merch = merchMapper.toEntity(request);

        // Step 2: Convert variant DTOs → entities
        List<MerchVariant> merchVariants = request.getMerchVariantRequestDto()
            .stream()
            .map(merchVariantMapper::toEntity)
            .toList();

        // Attach each variant back to merch
        merchVariants.forEach(variant -> variant.setMerch(merch));
        merch.setMerchVariantList(merchVariants);

        // Step 3: Save entity
        Merch savedMerch = merchRepository.save(merch);

        // Step 4: Convert back to response DTO
        MerchResponseDTO responseDTO = merchMapper.toResponseDTO(savedMerch);

        // Fill in the variants manually using MerchVariantMapper
        List<MerchVariantResponseDTO> variantResponseDTOs = savedMerch.getMerchVariantList()
            .stream()
            .map(merchVariantMapper::toResponseDTO)
            .toList();

        responseDTO.setVariants(variantResponseDTOs);

        return responseDTO;
    }


    
    @Override
    public List<MerchResponseDTO> getAllMerch() {
        return merchRepository.findAll()
                .stream()
                .map(merchMapper::toResponseDTO)
                .toList();
    }

    @Override
    public MerchResponseDTO getMerchById(Long id) {
        Merch merch = merchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merch not found with id: " + id));
        return merchMapper.toResponseDTO(merch);
    }

    @Override
    @Transactional
    public Map<String, Object> putMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO) {
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

        return Map.of("message", "Merch Updated Successfully",
                        "timestamp", LocalDateTime.now(),
                        "status", 200);    
        }

    @Override
    @Transactional
    public Map<String, Object> patchMerch(Long merchId, MerchUpdateRequestDTO merchUpdateRequestDTO) {
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


        // return the response
        return Map.of("message", "Merch Updated Successfully",
                        "timestamp", LocalDateTime.now(),
                        "status", 200);
    }
}