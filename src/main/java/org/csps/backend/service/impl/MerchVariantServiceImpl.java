package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.csps.backend.domain.dtos.request.InvalidRequestException;
import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.exception.MerchNotFoundException;
import org.csps.backend.exception.MerchVariantAlreadyExisted;
import org.csps.backend.exception.MerchVariantNotFoundException;
import org.csps.backend.mapper.MerchVariantMapper;
import org.csps.backend.repository.MerchRepository;
import org.csps.backend.repository.MerchVariantRepository;
import org.csps.backend.service.MerchVariantService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchVariantServiceImpl implements MerchVariantService {

    private final MerchVariantRepository merchVariantRepository;
    private final MerchVariantMapper merchVariantMapper;
    private final MerchRepository merchRepository;

    @Override
    public MerchVariantResponseDTO addMerchVariant(MerchVariantRequestDTO dto) {
        Merch merch = merchRepository.findById(dto.getMerchId())
                .orElseThrow(() -> new IllegalArgumentException("Merch not found"));



        MerchVariant merchVariant = merchVariantMapper.toEntity(dto);
        merchVariant.setMerch(merch);

        MerchVariant saved = merchVariantRepository.save(merchVariant);

        return merchVariantMapper.toResponseDTO(saved);
    }
    
    @Override
    public List<MerchVariantResponseDTO> addAllMerchVariant(List<MerchVariantRequestDTO> dtos) {
        List<MerchVariant> variants = dtos.stream().map(dto -> {
    
            MerchVariant variant = merchVariantMapper.toEntity(dto);
            return variant;
        }).toList();

        List<MerchVariant> saved = merchVariantRepository.saveAll(variants);
        return saved.stream()
                .map(merchVariantMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<MerchVariantResponseDTO> getAllMerchVariant() {
        return merchVariantRepository.findAll().stream()
                .map(merchVariantMapper::toResponseDTO)
                .toList();
    }


    @Override
    public MerchVariantResponseDTO addVariantToMerch(Long merchId, MerchVariantRequestDTO dto) {
        // Fetch existing merch
        Merch merch = merchRepository.findById(merchId)
                .orElseThrow(() -> new RuntimeException("Merch not found with id " + merchId));

                        boolean exists = merchVariantRepository.existsByMerchAndColorAndSize(merch, dto.getColor(), dto.getSize());
        if (exists) {
            throw new IllegalArgumentException("Variant with color " + dto.getColor() +
                    " and size " + dto.getSize() + " already exists for merch " + merch.getMerchName());
        }
        // Map dto to entity
        MerchVariant variant = merchVariantMapper.toEntity(dto);
        variant.setMerch(merch); // associate with merch

        MerchVariant saved = merchVariantRepository.save(variant);

        return merchVariantMapper.toResponseDTO(saved);
    }

    @Override
    public List<MerchVariantResponseDTO> getMerchVariantByMerchId(Long merchId) {
        return merchVariantRepository.findByMerchMerchId(merchId).stream()
                .map(merchVariantMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Map<String, Object> putMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO) {
        String color = merchVariantRequestDTO.getColor();
        ClothingSizing clothingSizing = merchVariantRequestDTO.getSize();
        Double price = merchVariantRequestDTO.getPrice();
        Integer stockQuantity = merchVariantRequestDTO.getStockQuantity();

        if (color.isEmpty() || clothingSizing == null || price == null || stockQuantity == null) {
            throw new InvalidRequestException("Invalid Credential");
        }
        
        Merch merch = merchRepository.findById(merchId)
                      .orElseThrow(() -> new MerchNotFoundException("Merch Not Found"));

        Long merchVariantId = merchVariantRequestDTO.getMerchVariantId();

        MerchVariant merchVariant = merchVariantRepository.findById(merchVariantId)
                .orElseThrow(() -> new MerchVariantNotFoundException("Merch Variant Not Found"));


        MerchType merchType = merch.getMerchType();

        boolean merchVariantAlreadyExisted = merchVariantRepository.existsByMerchAndColorAndSize(merch, color, clothingSizing);

        if (merchVariantAlreadyExisted) {
            throw new MerchVariantAlreadyExisted("Merch Variant Already Existed");
        }

        switch (merchType) {
            case CLOTHING -> {
                if (clothingSizing == null || price == null || stockQuantity == null) {
                    throw new InvalidRequestException("Invalid Credential");
                }

            }
            case PIN, STICKER, KEYCHAIN -> {
                if (price == null || stockQuantity == null) {
                    throw new InvalidRequestException("Invalid Credential");
                }
            }
            default -> throw new InvalidRequestException("Invalid Credential");
        }
        
        merchVariant.setColor(color);
        merchVariant.setSize(clothingSizing);
        merchVariant.setPrice(price);
        merchVariant.setStockQuantity(stockQuantity);

        merchVariantRepository.save(merchVariant);

        return Map.of("message", "Merch Variant Updated Successfully",
                        "timestamp", LocalDateTime.now(),
                        "status", 200);

        
    }

       @Override
    public Map<String, Object> patchMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO) {
        String color = merchVariantRequestDTO.getColor();
        ClothingSizing clothingSizing = merchVariantRequestDTO.getSize();
        Double price = merchVariantRequestDTO.getPrice();
        Integer stockQuantity = merchVariantRequestDTO.getStockQuantity();
        
        Merch merch = merchRepository.findById(merchId)
                      .orElseThrow(() -> new MerchNotFoundException("Merch Not Found"));

        Long merchVariantId = merchVariantRequestDTO.getMerchVariantId();

        MerchVariant merchVariant = merchVariantRepository.findById(merchVariantId)
                .orElseThrow(() -> new MerchVariantNotFoundException("Merch Variant Not Found"));


        MerchType merchType = merch.getMerchType();

        boolean merchVariantAlreadyExisted = merchVariantRepository.existsByMerchAndColorAndSize(merch, color, clothingSizing);

        if (merchVariantAlreadyExisted) {
            throw new MerchVariantAlreadyExisted("Merch Variant Already Existed");
        }

        switch (merchType) {
            case CLOTHING -> {
                if (clothingSizing == null || price == null || stockQuantity == null) {
                    throw new InvalidRequestException("Invalid Credential");
                }

            }
            case PIN, STICKER, KEYCHAIN -> {
                if (clothingSizing != null) {
                    throw new InvalidRequestException("Invalid Credential");
                }
                if (price == null || stockQuantity == null) {
                    throw new InvalidRequestException("Invalid Credential");
                }
            }
            default -> throw new InvalidRequestException("Invalid Credential");
        }
        
        merchVariant.setColor(color);
        merchVariant.setSize(clothingSizing);
        merchVariant.setPrice(price);
        merchVariant.setStockQuantity(stockQuantity);

        merchVariantRepository.save(merchVariant);

        return Map.of("message", "Merch Variant Updated Successfully",
                        "timestamp", LocalDateTime.now(),
                        "status", 200);

        
    }



}
