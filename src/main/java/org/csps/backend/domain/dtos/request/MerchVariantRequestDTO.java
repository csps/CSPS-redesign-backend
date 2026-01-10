package org.csps.backend.domain.dtos.request;


import org.csps.backend.domain.enums.ClothingSizing;

import lombok.Data;
@Data
public class MerchVariantRequestDTO {
    private Long merchId;          // reference to parent Merch
    private String color;
    private ClothingSizing size;
    private Double price;
    private Integer stockQuantity;
    private String s3ImageKey;     // S3 object key (optional - image uploaded separately)
}
