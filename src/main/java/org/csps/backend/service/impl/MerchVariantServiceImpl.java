package org.csps.backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.csps.backend.domain.dtos.request.MerchVariantRequestDTO;
import org.csps.backend.domain.dtos.request.MerchVariantUpdateRequestDTO;
import org.csps.backend.domain.dtos.response.MerchVariantResponseDTO;
import org.csps.backend.domain.entities.Merch;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.MerchNotFoundException;
import org.csps.backend.exception.MerchVariantAlreadyExisted;
import org.csps.backend.exception.MerchVariantNotFoundException;
import org.csps.backend.mapper.MerchVariantMapper;
import org.csps.backend.repository.MerchRepository;
import org.csps.backend.repository.MerchVariantRepository;
import org.csps.backend.service.MerchVariantService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchVariantServiceImpl implements MerchVariantService {

    private final MerchVariantRepository merchVariantRepository;
    private final MerchVariantMapper merchVariantMapper;
    private final MerchRepository merchRepository;

    @Override
    @Transactional
    public MerchVariantResponseDTO addMerchVariant(MerchVariantRequestDTO dto) {
        Merch merch = merchRepository.findById(dto.getMerchId())
                .orElseThrow(() -> new IllegalArgumentException("Merch not found"));
        MerchType merchType = merch.getMerchType();

        switch (merchType) {
            case CLOTHING -> {
                if (dto.getColor() == null || dto.getSize() == null) {
                    throw new InvalidRequestException("color and size required for clothing");
                }
                if (merchVariantRepository.existsByMerchAndColorAndSize(merch, dto.getColor(), dto.getSize())) {
                    throw new MerchVariantAlreadyExisted("Variant with color " + dto.getColor() + " and size " + dto.getSize() + " already exists for merch " + merch.getMerchName());
                }
            }
            case PIN, STICKER, KEYCHAIN -> {
                if (dto.getDesign() == null) {
                    throw new InvalidRequestException("design required for this merch type");
                }
                if (merchVariantRepository.existsByMerchAndDesign(merch, dto.getDesign())) {
                    throw new MerchVariantAlreadyExisted("Variant with design " + dto.getDesign() + " already exists for merch " + merch.getMerchName());
                }
            }
            default -> throw new InvalidRequestException("Unsupported merch type");
        }

        MerchVariant merchVariant = merchVariantMapper.toEntity(dto);
        merchVariant.setMerch(merch);

        MerchVariant saved = merchVariantRepository.save(merchVariant);

        return merchVariantMapper.toResponseDTO(saved);
    }
    
    @Override
    @Transactional
    public List<MerchVariantResponseDTO> addAllMerchVariant(List<MerchVariantRequestDTO> dtos) {
        List<MerchVariant> variants = dtos.stream().map(dto -> {
            Merch merch = merchRepository.findById(dto.getMerchId())
                    .orElseThrow(() -> new IllegalArgumentException("Merch not found"));

            MerchType merchType = merch.getMerchType();

            switch (merchType) {
                case CLOTHING -> {
                    if (dto.getColor() == null || dto.getSize() == null) {
                        throw new InvalidRequestException("color and size required for clothing");
                    }
                    if (merchVariantRepository.existsByMerchAndColorAndSize(merch, dto.getColor(), dto.getSize())) {
                        throw new MerchVariantAlreadyExisted("Variant with color " + dto.getColor() + " and size " + dto.getSize() + " already exists for merch " + merch.getMerchName());
                    }
                }
                case PIN, STICKER, KEYCHAIN -> {
                    if (dto.getDesign() == null) {
                        throw new InvalidRequestException("design required for this merch type");
                    }
                    if (merchVariantRepository.existsByMerchAndDesign(merch, dto.getDesign())) {
                        throw new MerchVariantAlreadyExisted("Variant with design " + dto.getDesign() + " already exists for merch " + merch.getMerchName());
                    }
                }
                default -> throw new InvalidRequestException("Unsupported merch type");
            }

            MerchVariant variant = merchVariantMapper.toEntity(dto);
            variant.setMerch(merch);
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
    @Transactional
    public MerchVariantResponseDTO addVariantToMerch(Long merchId, MerchVariantRequestDTO dto) {
        // Fetch existing merch
        Merch merch = merchRepository.findById(merchId)
                .orElseThrow(() -> new RuntimeException("Merch not found with id " + merchId));
        MerchType merchType = merch.getMerchType();

        switch (merchType) {
            case CLOTHING -> {
                if (dto.getColor() == null || dto.getSize() == null) {
                    throw new InvalidRequestException("color and size required for clothing");
                }
                if (merchVariantRepository.existsByMerchAndColorAndSize(merch, dto.getColor(), dto.getSize())) {
                    throw new MerchVariantAlreadyExisted("Variant with color " + dto.getColor() + " and size " + dto.getSize() + " already exists for merch " + merch.getMerchName());
                }
            }
            case PIN, STICKER, KEYCHAIN -> {
                if (dto.getDesign() == null) {
                    throw new InvalidRequestException("design required for this merch type");
                }
                if (merchVariantRepository.existsByMerchAndDesign(merch, dto.getDesign())) {
                    throw new MerchVariantAlreadyExisted("Variant with design " + dto.getDesign() + " already exists for merch " + merch.getMerchName());
                }
            }
            default -> throw new InvalidRequestException("Unsupported merch type");
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
    public MerchVariantResponseDTO getMerchVariantBySize(ClothingSizing size, Long merchId) {
        // Keep behaviour for backwards compatibility: returns a variant by size (first found)
        MerchVariant merchVariant = merchVariantRepository.findByMerchMerchIdAndSize(merchId, size)
            .orElseThrow(() -> new MerchVariantNotFoundException("Merch Variant Not Found"));

        return merchVariantMapper.toResponseDTO(merchVariant);
    }

    @Override
    public java.util.List<ClothingSizing> getAvailableSizesForColor(Long merchId, String color) {
        Merch merch = merchRepository.findById(merchId)
                .orElseThrow(() -> new MerchNotFoundException("Merch Not Found"));

        if (merch.getMerchType() != MerchType.CLOTHING) {
            throw new InvalidRequestException("Available sizes/colors only applicable for clothing merch");
        }

        if (color == null || color.isEmpty()) {
            throw new InvalidRequestException("color is required");
        }

        java.util.List<ClothingSizing> sizes = merchVariantRepository.findAvailableSizesByMerchIdAndColor(merchId, color);
        return sizes == null ? java.util.List.of() : sizes;
    }

    @Override
    public org.csps.backend.domain.dtos.response.ClothingResponseDTO getClothingBySize(Long merchId, ClothingSizing size) {
        Merch merch = merchRepository.findById(merchId)
                .orElseThrow(() -> new MerchNotFoundException("Merch Not Found"));

        if (merch.getMerchType() != MerchType.CLOTHING) {
            throw new InvalidRequestException("Available sizes/colors only applicable for clothing merch");
        }

        if (size == null) {
            throw new InvalidRequestException("size is required");
        }

        java.util.List<String> colors = merchVariantRepository.findAvailableColorsByMerchIdAndSize(merchId, size);
        if (colors == null) colors = java.util.List.of();

        return org.csps.backend.domain.dtos.response.ClothingResponseDTO.builder()
                .merchId(merchId)
                .size(size)
                .availableColors(colors)
                .build();
    }

        @Override
        public MerchVariantResponseDTO getMerchVariant(Long merchId, String color, ClothingSizing size, String design) {
            Merch merch = merchRepository.findById(merchId)
                .orElseThrow(() -> new MerchNotFoundException("Merch Not Found"));

            MerchType merchType = merch.getMerchType();

            if (merchType == MerchType.CLOTHING) {
                if (color == null || size == null) throw new InvalidRequestException("color and size required for clothing");
                MerchVariant mv = merchVariantRepository.findByMerchMerchIdAndColorAndSize(merchId, color, size)
                    .orElseThrow(() -> new MerchVariantNotFoundException("Merch Variant Not Found"));
                return merchVariantMapper.toResponseDTO(mv);
            } else {
                if (design == null) throw new InvalidRequestException("design required for this merch type");
                MerchVariant mv = merchVariantRepository.findByMerchMerchIdAndDesign(merchId, design)
                    .orElseThrow(() -> new MerchVariantNotFoundException("Merch Variant Not Found"));
                return merchVariantMapper.toResponseDTO(mv);
            }
        }


    @Override
    @Transactional
    public MerchVariantResponseDTO putMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO) {
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

        // check existing variant but ignore current record
        if (merchType == MerchType.CLOTHING) {
            Optional<MerchVariant> existing = merchVariantRepository.findByMerchMerchIdAndColorAndSize(merchId, color, clothingSizing);
            if (existing.isPresent() && !existing.get().getMerchVariantId().equals(merchVariantId)) {
                throw new MerchVariantAlreadyExisted("Merch Variant Already Existed");
            }
        } else {
            Optional<MerchVariant> existing = merchVariantRepository.findByMerchMerchIdAndDesign(merchId, merchVariantRequestDTO.getDesign());
            if (existing.isPresent() && !existing.get().getMerchVariantId().equals(merchVariantId)) {
                throw new MerchVariantAlreadyExisted("Merch Variant Already Existed");
            }
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
        merchVariant.setDesign(merchVariantRequestDTO.getDesign());
        merchVariant.setPrice(price);
        merchVariant.setStockQuantity(stockQuantity);

        merchVariantRepository.save(merchVariant);

        MerchVariantResponseDTO merchVariantResponseDTO = merchVariantMapper.toResponseDTO(merchVariant);

        return merchVariantResponseDTO;
        
    }

        @Override
        @Transactional
        public MerchVariantResponseDTO patchMerchVariant(Long merchId, MerchVariantUpdateRequestDTO merchVariantRequestDTO) {
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

        // check existing variant but ignore current record
        if (merchType == MerchType.CLOTHING) {
            java.util.Optional<MerchVariant> existing = merchVariantRepository.findByMerchMerchIdAndColorAndSize(merchId, color, clothingSizing);
            if (existing.isPresent() && !existing.get().getMerchVariantId().equals(merchVariantId)) {
                throw new MerchVariantAlreadyExisted("Merch Variant Already Existed");
            }
        } else {
            java.util.Optional<MerchVariant> existing = merchVariantRepository.findByMerchMerchIdAndDesign(merchId, merchVariantRequestDTO.getDesign());
            if (existing.isPresent() && !existing.get().getMerchVariantId().equals(merchVariantId)) {
                throw new MerchVariantAlreadyExisted("Merch Variant Already Existed");
            }
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
        merchVariant.setDesign(merchVariantRequestDTO.getDesign());
        merchVariant.setPrice(price);
        merchVariant.setStockQuantity(stockQuantity);

        merchVariantRepository.save(merchVariant);

        MerchVariantResponseDTO merchVariantResponseDTO = merchVariantMapper.toResponseDTO(merchVariant);

        return merchVariantResponseDTO;        
    }
}
