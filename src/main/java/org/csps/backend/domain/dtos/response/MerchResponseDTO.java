package org.csps.backend.domain.dtos.response;

import java.util.List;

import org.csps.backend.domain.enums.MerchType;

import com.fasterxml.jackson.annotation.JsonAlias;

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
    private MerchType merchType;

    @JsonAlias("variants")
    private List<MerchVariantResponseDTO> variants; // include variants here
}
