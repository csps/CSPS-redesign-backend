package org.csps.backend.domain.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPostRequestDTO {
    
    
    @NotEmpty(message = "At least one order item is required")
    @Valid
    private List<OrderItemRequestDTO> orderItems;
}

