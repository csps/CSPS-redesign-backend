package org.csps.backend.domain.dtos.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponseDTO {
    
    private Long orderItemId;
    
    private Long orderId;
    
    private String merchName;
    
    private String color;
    
    private String design;
    
    private String size;
        
    private Integer quantity;
        
    private Double totalPrice;
    
    private String s3ImageKey;
    
    private LocalDateTime createdAt;
        
    private LocalDateTime updatedAt;
}
