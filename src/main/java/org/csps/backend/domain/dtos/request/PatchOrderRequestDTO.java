package org.csps.backend.domain.dtos.request;

import org.csps.backend.domain.enums.OrderStatus;

import lombok.Data;

@Data
public class PatchOrderRequestDTO {
    public OrderStatus orderStatus;
}
