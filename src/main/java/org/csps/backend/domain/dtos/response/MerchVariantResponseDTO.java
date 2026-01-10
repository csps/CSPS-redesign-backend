package org.csps.backend.domain.dtos.response;

import org.csps.backend.domain.enums.ClothingSizing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchVariantResponseDTO {
    private Long merchVariantId;
    private String color;
    private ClothingSizing size;
    private Double price;
    private Integer stockQuantity;

    private Long merchId; // reference to MerchResponseDto (to avoid nesting)

    private String s3ImageKey;     // S3 object key - frontend constructs URL as {S3_BASE_URL}/{s3ImageKey}
}
