package org.csps.backend.domain.dtos.request;

import java.util.List;

import org.csps.backend.domain.enums.ClothingSizing;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchVariantRequestDTO {
    private String color;
    private String design;

    private String s3ImageKey; 

    @JsonIgnore
    private MultipartFile variantImage; // For image upload

    private List<MerchVariantItemRequestDTO> variantItems; // List of size/stock items
}
