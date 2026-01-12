package org.csps.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.dtos.request.OrderPostRequestDTO;
import org.csps.backend.domain.dtos.response.OrderResponseDTO;
import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.OrderNotFoundException;
import org.csps.backend.exception.StudentNotFoundException;
import org.csps.backend.mapper.OrderMapper;
import org.csps.backend.repository.OrderRepository;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.service.OrderService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(String studentId, OrderPostRequestDTO orderPostRequestDTO) {
        if (studentId == null || studentId.isEmpty()) {
            throw new InvalidRequestException("Student ID is required");
        }
        
        if (orderPostRequestDTO == null) {
            throw new InvalidRequestException("Order request is required");
        }
        
        // Validate student exists
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
        
        
        // Create order
        Order order = orderMapper.toEntity(orderPostRequestDTO);

        order.setUpdatedAt(LocalDateTime.now());

        order.setStudent(student);
        
        
        Order savedOrder = orderRepository.save(order);
        
        // Load with items and return
        Order loadedOrder = orderRepository.findById(savedOrder.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        return orderMapper.toResponseDTO(loadedOrder);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDTO)
                .toList();
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
