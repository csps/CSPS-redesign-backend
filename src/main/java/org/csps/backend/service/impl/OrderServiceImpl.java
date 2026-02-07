package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.dtos.request.OrderItemRequestDTO;
import org.csps.backend.domain.dtos.request.OrderPostRequestDTO;
import org.csps.backend.domain.dtos.response.OrderItemResponseDTO;
import org.csps.backend.domain.dtos.response.OrderResponseDTO;
import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.enums.OrderStatus;
import org.csps.backend.exception.CartItemNotFoundException;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.OrderNotFoundException;
import org.csps.backend.exception.StudentNotFoundException;
import org.csps.backend.mapper.OrderMapper;
import org.csps.backend.repository.OrderRepository;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.service.CartItemService;
import org.csps.backend.service.OrderItemService;
import org.csps.backend.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StudentRepository studentRepository;
    private final OrderItemService orderItemService;
    private final CartItemService cartItemService;


    @Override
    @Transactional
    public OrderResponseDTO createOrder(String studentId, OrderPostRequestDTO orderRequests) {
        if (studentId == null || studentId.isEmpty()) {
            throw new InvalidRequestException("Student ID is required");
        }
        
        if (orderRequests == null || orderRequests.getOrderItems() == null || orderRequests.getOrderItems().isEmpty()) {
            throw new InvalidRequestException("At least one order request is required");
        }
        
        // Validate student exists
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
        
        // Create order
        Order order = Order.builder()
                .student(student)
                .orderDate(LocalDateTime.now())
                .totalPrice(0.0) // Will be updated after adding order items
                .updatedAt(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING) // Default status for new orders
                .quantity(0)
                .build();

        // Save order first to get the generated orderId
        Order savedOrder = orderRepository.save(order);

        List<OrderItemRequestDTO> orderItemRequests = orderRequests.getOrderItems();
        
        // Now set the orderId on all item requests
        orderItemRequests.forEach(req -> {
            req.setOrderId(savedOrder.getOrderId());
        });

        double totalPrice = 0.0;
        for (OrderItemRequestDTO itemRequest : orderItemRequests) {
            OrderItemResponseDTO orderItemResponse = orderItemService.createOrderItem(itemRequest);
            totalPrice += orderItemResponse.getTotalPrice();
            
            // Remove the item from cart after successful order item creation
            try {
                cartItemService.removeCartItem(studentId, itemRequest.getMerchVariantItemId());
            } catch (CartItemNotFoundException e) {
                // Item not in cart is OK - might have been removed already
                System.out.println("Item not found in cart (already removed): " + itemRequest.getMerchVariantItemId());
            } catch (Exception e) {
                // Log other failures but don't fail the order
                System.err.println("Error: Failed to remove item from cart after ordering: " + e.getMessage());
                e.printStackTrace();
            }
        }

        savedOrder.setTotalPrice(totalPrice);
        orderRepository.save(savedOrder);
        
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Page<OrderResponseDTO> getAllOrdersPaginated(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::toResponseDTO);
    }

    @Override
    public OrderResponseDTO getOrderById(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new InvalidRequestException("Invalid order ID");
        }
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        return orderMapper.toResponseDTO(order);
    }

    @Override
    public Page<OrderResponseDTO> getAllOrdersPaginatedSortByDate(Pageable pageable) {
        Page<Order> orders = orderRepository.findAllByOrderByOrderDateDesc(pageable);
        return orders.map(orderMapper::toResponseDTO);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStudentId(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            throw new InvalidRequestException("Student ID is required");
        }
        
        // Verify student exists
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException("Student not found");
        }
        
        return orderRepository.findByStudentId(studentId)
                .stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Page<OrderResponseDTO> getOrdersByStudentIdPaginated(String studentId, Pageable pageable) {
        if (studentId == null || studentId.isEmpty()) {
            throw new InvalidRequestException("Student ID is required");
        }
        
        // Verify student exists
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException("Student not found");
        }
        
        Page<Order> orders = orderRepository.findByStudentId(studentId, pageable);
        return orders.map(orderMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new InvalidRequestException("Invalid order ID");
        }
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        // Delete cascades to order items due to orphanRemoval = true
        orderRepository.delete(order);
    }
}

