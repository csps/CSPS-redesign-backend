package org.csps.backend.domain.dtos.request;

import java.util.ArrayList;
import java.util.List;

import org.csps.backend.domain.enums.MerchType;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MerchRequestDTO {
    
    @NotBlank(message = "Merchandise name is required")
    @Size(max = 100, message = "Merchandise name must not exceed 100 characters")
    private String merchName;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Merchandise type is required")
    private MerchType merchType;
    
    @JsonProperty("variants")
    @Valid
    private List<MerchVariantRequestDTO> merchVariantRequestDto = new ArrayList<>();
}
