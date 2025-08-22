package org.csps.backend.domain.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchResponseDTO {
    private Long merchId;
    private String merchName;
    private String description;
    
}
