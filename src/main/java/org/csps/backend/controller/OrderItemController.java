package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.OrderItemRequestDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.OrderItemResponseDTO;
import org.csps.backend.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    
    private final OrderItemService orderItemService;
    
    /**
     * Create a new order item.
     * Only admins can create order items.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<OrderItemResponseDTO>> createOrderItem(
            @Valid @RequestBody OrderItemRequestDTO requestDTO) {
        OrderItemResponseDTO responseDTO = orderItemService.createOrderItem(requestDTO);
        return GlobalResponseBuilder.buildResponse("Order item created successfully", responseDTO, HttpStatus.CREATED);
    }
    
    /**
     * Get order item by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<OrderItemResponseDTO>> getOrderItemById(
            @PathVariable Long id) {
        OrderItemResponseDTO responseDTO = orderItemService.getOrderItemById(id);
        return GlobalResponseBuilder.buildResponse("Order item retrieved successfully", responseDTO, HttpStatus.OK);
    }
    
    /**
     * Get all order items for a specific order.
     */
    @GetMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<List<OrderItemResponseDTO>>> getOrderItemsByOrderId(
            @RequestParam Long orderId) {
        List<OrderItemResponseDTO> responseDTOs = orderItemService.getOrderItemsByOrderId(orderId);
        return GlobalResponseBuilder.buildResponse("Order items retrieved successfully", responseDTOs, HttpStatus.OK);
    }
    
    /**
     * Delete an order item.
     * Only admins can delete order items.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<Void>> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return GlobalResponseBuilder.buildResponse("Order item deleted successfully", null, HttpStatus.NO_CONTENT);
    }
}
