package org.csps.backend.domain.dtos.response;

import org.csps.backend.domain.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderResponseDTO {

    private Long orderId;
    private String merchVariantName;
    private String merchVariantColor;
    private String merchVariantSize;
    private int quantity;
    private String studentName;
    private Double totalPrice;
    private OrderStatus orderStatus;
}
