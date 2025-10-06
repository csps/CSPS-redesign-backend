package org.csps.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.dtos.request.OrderPostRequestDTO;
import org.csps.backend.domain.dtos.request.PatchOrderRequestDTO;
import org.csps.backend.domain.dtos.response.OrderResponseDTO;
import org.csps.backend.domain.entities.MerchVariant;
import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.enums.OrderStatus;
import org.csps.backend.exception.InvalidOrderStatusTransitionException;
import org.csps.backend.exception.MerchVariantNotFoundException;
import org.csps.backend.exception.OrderNotFoundException;
import org.csps.backend.exception.OutOfStockException;
import org.csps.backend.exception.StudentNotFoundException;
import org.csps.backend.mapper.OrderMapper;
import org.csps.backend.repository.MerchVariantRepository;
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
    private final MerchVariantRepository merchVariantRepository;

    // Business Logic Validation Methods
    private void validateOrderCreation(OrderPostRequestDTO orderPostRequestDTO, MerchVariant merchVariant) {
        // Validate quantity is positive
        if (orderPostRequestDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        // Validate stock availability
        if (merchVariant.getStockQuantity() < orderPostRequestDTO.getQuantity()) {
            throw new OutOfStockException("Insufficient stock. Available: " + merchVariant.getStockQuantity() + 
                                       ", Requested: " + orderPostRequestDTO.getQuantity());
        }
        
        // Validate merch variant is active/available
        if (merchVariant.getStockQuantity() == 0) {
            throw new OutOfStockException("Item is out of stock");
        }
    }

    private void validateOrderStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.TO_BE_CLAIMED && newStatus != OrderStatus.CANCELLED) {
                    throw new InvalidOrderStatusTransitionException(
                        "Cannot change status from " + currentStatus + " to " + newStatus);
                }
                break;
            case TO_BE_CLAIMED:
                if (newStatus != OrderStatus.CLAIMED && newStatus != OrderStatus.CANCELLED) {
                    throw new InvalidOrderStatusTransitionException(
                        "Cannot change status from " + currentStatus + " to " + newStatus);
                }
                break;
            case CLAIMED:
                throw new InvalidOrderStatusTransitionException(
                    "Cannot modify order with status " + currentStatus + ". Order is already claimed.");
            case CANCELLED:
                throw new InvalidOrderStatusTransitionException(
                    "Cannot modify order with status " + currentStatus + ". Order is cancelled.");
            default:
                throw new InvalidOrderStatusTransitionException("Invalid current status: " + currentStatus);
        }
    }

    private void validateOrderModification(Order order) {
        // Cannot modify orders that are already claimed or cancelled
        if (order.getOrderStatus() == OrderStatus.CLAIMED) {
            throw new InvalidOrderStatusTransitionException("Cannot modify order that is already claimed");
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusTransitionException("Cannot modify order that is cancelled");
        }
    }

    @Override
    @Transactional
    public OrderResponseDTO postOrder(String studentId, OrderPostRequestDTO orderPostRequestDTO) {
        Long merchVariantId = orderPostRequestDTO.getMerchVariantId();

        if (merchVariantId == null) {
            throw new MerchVariantNotFoundException("Merch Variant not found");
        }   

        MerchVariant merchVariant = merchVariantRepository.findById(merchVariantId)
                .orElseThrow(() -> new MerchVariantNotFoundException("Merch Variant not found"));
        
        // Apply business logic validation
        validateOrderCreation(orderPostRequestDTO, merchVariant);

        merchVariant.setStockQuantity(merchVariant.getStockQuantity() - orderPostRequestDTO.getQuantity());

        merchVariantRepository.save(merchVariant);

        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
            
        Order order = orderMapper.toEntity(orderPostRequestDTO);
        order.setTotalPrice(merchVariant.getPrice() * orderPostRequestDTO.getQuantity());
        order.setStudent(student);
        order.setMerchVariant(merchVariant);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        return orderMapper.toResponseDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<OrderResponseDTO> orderResponseDTOs = orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDTO)
                .toList();
        return orderResponseDTOs;
    }

    @Override
    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        OrderResponseDTO orderResponseDTO = orderMapper.toResponseDTO(order);
        return orderResponseDTO;
    }

    @Override
    @Transactional
    public OrderResponseDTO deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        // Validate order can be deleted (only PENDING and CANCELLED orders can be deleted)
        if (order.getOrderStatus() == OrderStatus.CLAIMED) {
            throw new InvalidOrderStatusTransitionException("Cannot delete order that is already claimed");
        }
        if (order.getOrderStatus() == OrderStatus.TO_BE_CLAIMED) {
            throw new InvalidOrderStatusTransitionException("Cannot delete order that is ready to be claimed");
        }
        
        orderRepository.delete(order);

        OrderResponseDTO orderResponseDTO = orderMapper.toResponseDTO(order);
        return orderResponseDTO;
    }
    @Override
    @Transactional
    public OrderResponseDTO patchOrder(Long orderId, PatchOrderRequestDTO patchOrderRequestDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        // Validate order can be modified
        validateOrderModification(order);
        
        // Validate status transition
        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newStatus = patchOrderRequestDTO.getOrderStatus();
        
        if (currentStatus != newStatus) {
            validateOrderStatusTransition(currentStatus, newStatus);
        }
        
        order.setOrderStatus(patchOrderRequestDTO.getOrderStatus());
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        OrderResponseDTO orderResponseDTO = orderMapper.toResponseDTO(order);
        return orderResponseDTO;
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStudentId(String studentId) {
        List<OrderResponseDTO> orderResponseDTOs = orderRepository.findByStudentId(studentId)
                .stream()
                .map(orderMapper::toResponseDTO)
                .toList();
        return orderResponseDTOs;
    }

    @Override
    
    public List<OrderResponseDTO> getOrdersByOrderStatus(OrderStatus orderStatus) {
        List<OrderResponseDTO> orderResponseDTOs = orderRepository.findByOrderStatus(orderStatus)
                .stream()
                .map(orderMapper::toResponseDTO)
                .toList();
        return orderResponseDTOs;
    }

}
