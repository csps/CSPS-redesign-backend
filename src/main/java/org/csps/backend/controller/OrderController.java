package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.OrderPostRequestDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.OrderResponseDTO;
import org.csps.backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;

    /**
     * Create a new order.
     * Student can only create orders for themselves.
     * After creation, add items via /api/order-items endpoint.
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<OrderResponseDTO>> createOrder(
            @AuthenticationPrincipal String studentId,
            @Valid @RequestBody OrderPostRequestDTO orderPostRequestDTO) {
        OrderResponseDTO responseDTO = orderService.createOrder(studentId, orderPostRequestDTO);
        return GlobalResponseBuilder.buildResponse("Order created successfully", responseDTO, HttpStatus.CREATED);
    }

    /**
     * Get all orders (admin only).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<List<OrderResponseDTO>>> getAllOrders() {
        List<OrderResponseDTO> responseDTOs = orderService.getAllOrders();
        return GlobalResponseBuilder.buildResponse("Orders retrieved successfully", responseDTOs, HttpStatus.OK);
    }

    /**
     * Get order by ID.
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<OrderResponseDTO>> getOrderById(
            @PathVariable Long orderId) {
        OrderResponseDTO responseDTO = orderService.getOrderById(orderId);
        return GlobalResponseBuilder.buildResponse("Order retrieved successfully", responseDTO, HttpStatus.OK);
    }

    /**
     * Get all orders for the authenticated student.
     */
    @GetMapping("/student/my-orders")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<List<OrderResponseDTO>>> getMyOrders(
            @AuthenticationPrincipal String studentId) {
        List<OrderResponseDTO> responseDTOs = orderService.getOrdersByStudentId(studentId);
        return GlobalResponseBuilder.buildResponse("Orders retrieved successfully", responseDTOs, HttpStatus.OK);
    }

    /**
     * Delete order.
     * Only admins can delete orders.
     */
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<Void>> deleteOrder(
            @PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return GlobalResponseBuilder.buildResponse("Order deleted successfully", null, HttpStatus.NO_CONTENT);
    }
}
