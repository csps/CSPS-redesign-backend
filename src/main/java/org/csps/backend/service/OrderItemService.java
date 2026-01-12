package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.request.OrderItemRequestDTO;
import org.csps.backend.domain.dtos.response.OrderItemResponseDTO;

public interface OrderItemService {
    
    /**
     * Create a new order item.
     */
    OrderItemResponseDTO createOrderItem(OrderItemRequestDTO orderItemRequestDTO);
    
    /**
     * Get order item by ID.
     */
    OrderItemResponseDTO getOrderItemById(Long id);
    
    /**
     * Get all order items for a specific order.
     */
    List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId);
  
    /**
     * Delete order item.
     */
    void deleteOrderItem(Long id);
}
