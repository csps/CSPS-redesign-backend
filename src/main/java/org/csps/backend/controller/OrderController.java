package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.OrderPostRequestDTO;
import org.csps.backend.domain.dtos.request.PatchOrderRequestDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.OrderResponseDTO;
import org.csps.backend.domain.enums.OrderStatus;
import org.csps.backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService  orderService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<OrderResponseDTO>> addOrder(@AuthenticationPrincipal String studentId, @Valid @RequestBody OrderPostRequestDTO orderPostRequestDTO) {
        OrderResponseDTO orderResponseDTO = orderService.postOrder(studentId, orderPostRequestDTO);
        return GlobalResponseBuilder.buildResponse("Order added successfully", orderResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<List<OrderResponseDTO>>> getAllOrders() {
        List<OrderResponseDTO> orderResponseDTOs = orderService.getAllOrders();
        return GlobalResponseBuilder.buildResponse("Orders retrieved successfully", orderResponseDTOs, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<OrderResponseDTO>> getOrderById(@PathVariable Long orderId) {
        OrderResponseDTO orderResponseDTO = orderService.getOrderById(orderId);
        return GlobalResponseBuilder.buildResponse("Order retrieved successfully", orderResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<List<OrderResponseDTO>>> getOrdersByStudentId(@AuthenticationPrincipal String studentId) {
        List<OrderResponseDTO> orderResponseDTOs = orderService.getOrdersByStudentId(studentId);
        return GlobalResponseBuilder.buildResponse("Orders retrieved successfully", orderResponseDTOs, HttpStatus.OK);
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<OrderResponseDTO>> patchOrder(@PathVariable Long orderId, @Valid @RequestBody PatchOrderRequestDTO patchOrderRequestDTO) {
        OrderResponseDTO orderResponseDTO = orderService.patchOrder(orderId, patchOrderRequestDTO);
        return GlobalResponseBuilder.buildResponse("Order patched successfully", orderResponseDTO, HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<List<OrderResponseDTO>>> getOrdersByOrderStatus(@RequestParam OrderStatus status) {
        List<OrderResponseDTO> orderResponseDTOs = orderService.getOrdersByOrderStatus(status);
        return GlobalResponseBuilder.buildResponse("Orders retrieved successfully", orderResponseDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponseBuilder<OrderResponseDTO>> deleteOrder(@PathVariable Long orderId) {
        OrderResponseDTO orderResponseDTO = orderService.deleteOrder(orderId);
        return GlobalResponseBuilder.buildResponse("Order deleted successfully", orderResponseDTO, HttpStatus.OK);
    }
}
