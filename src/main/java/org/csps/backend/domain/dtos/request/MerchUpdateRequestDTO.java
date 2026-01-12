package org.csps.backend.domain.dtos.request;

import org.csps.backend.domain.enums.MerchType;

import lombok.Data;

@Data
public class MerchUpdateRequestDTO {
    private String merchName;
    private String description;
    private MerchType merchType;
}
