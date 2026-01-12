package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.response.OrderResponseDTO;
import org.csps.backend.domain.dtos.request.OrderPostRequestDTO;

public interface OrderService {

    /**
     * Create a new order for a student.
     * This creates an Order and delegates OrderItem creation to OrderItemService.
     */
    OrderResponseDTO createOrder(String studentId, OrderPostRequestDTO orderPostRequestDTO);
    
    /**
     * Get all orders (admin only).
     */
    List<OrderResponseDTO> getAllOrders();
    
    /**
     * Get order by ID.
     */
    OrderResponseDTO getOrderById(Long orderId);
    
    /**
     * Get all orders for a specific student.
     */
    List<OrderResponseDTO> getOrdersByStudentId(String studentId);
    
    /**
     * Delete order and all its items.
     */
    void deleteOrder(Long orderId);
}

