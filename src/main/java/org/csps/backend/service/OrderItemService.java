package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.request.OrderItemRequestDTO;
import org.csps.backend.domain.dtos.response.OrderItemResponseDTO;
import org.csps.backend.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Filter by order status
     *
     * @param pageable
     * @return
     */
    Page<OrderItemResponseDTO> getOrderItemsByStatus(OrderStatus status, Pageable pageable);
    
    /**
     * Get all order items for a specific order.
     */
    List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId);
    
    /**
     * Get paginated order items for a specific order.
     */
    Page<OrderItemResponseDTO> getOrderItemsByOrderIdPaginated(Long orderId, Pageable pageable);
    

    /**
     * Update order item status.
     */
    OrderItemResponseDTO updateOrderItemStatus(Long id, OrderStatus status);


    
    /**
     * Delete order item.
     */
    void deleteOrderItem(Long id);
}
