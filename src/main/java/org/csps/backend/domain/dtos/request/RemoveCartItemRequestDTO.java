package org.csps.backend.domain.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemoveCartItemRequestDTO {
    private String cartId;
    private Long merchVariantId;
}
