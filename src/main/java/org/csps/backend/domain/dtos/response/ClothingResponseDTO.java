package org.csps.backend.domain.dtos.response;

import java.util.List;

import org.csps.backend.domain.enums.ClothingSizing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClothingResponseDTO {
    private Long merchId;
    private ClothingSizing size;
    private List<String> availableColors;
}