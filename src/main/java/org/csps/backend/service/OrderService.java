package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.request.OrderPostRequestDTO;
import org.csps.backend.domain.dtos.request.PatchOrderRequestDTO;
import org.csps.backend.domain.dtos.response.OrderResponseDTO;
import org.csps.backend.domain.enums.OrderStatus;

public interface OrderService {

    OrderResponseDTO postOrder(String studentId, OrderPostRequestDTO orderPostRequestDTO);
    List<OrderResponseDTO> getAllOrders();
    OrderResponseDTO getOrderById(Long orderId);
    OrderResponseDTO deleteOrder(Long orderId);
    OrderResponseDTO patchOrder(Long orderId, PatchOrderRequestDTO patchOrderRequestDTO);
    List<OrderResponseDTO> getOrdersByStudentId(String studentId);
    List<OrderResponseDTO> getOrdersByOrderStatus(OrderStatus orderStatus);
}
