package org.csps.backend.domain.dtos.response;

import java.time.LocalDate;
import java.util.List;

import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.domain.enums.OrderStatus;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long orderId;
    
    private String studentName;
    
    private Double totalPrice;
    
    @JsonAlias("order_date")
    private LocalDate orderDate;
    


    @JsonAlias("order_items")
    private List<OrderItemResponseDTO> orderItems;
}

