package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.dtos.request.OrderItemRequestDTO;
import org.csps.backend.domain.dtos.response.OrderItemResponseDTO;
import org.csps.backend.domain.entities.MerchVariantItem;
import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.entities.OrderItem;
import org.csps.backend.domain.enums.OrderStatus;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.OrderItemNotFoundException;
import org.csps.backend.exception.OrderNotFoundException;
import org.csps.backend.mapper.OrderItemMapper;
import org.csps.backend.repository.MerchVariantItemRepository;
import org.csps.backend.repository.OrderItemRepository;
import org.csps.backend.repository.OrderRepository;
import org.csps.backend.service.OrderItemService;
import org.csps.backend.service.OrderNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MerchVariantItemRepository merchVariantItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderNotificationService orderNotificationService;
    
    @Override
    @Transactional
    public OrderItemResponseDTO createOrderItem(OrderItemRequestDTO orderItemRequestDTO) {
        if (orderItemRequestDTO == null) {
            throw new InvalidRequestException("Order item request is required");
        }
        
        /* validate order exists */
        Order order = orderRepository.findById(orderItemRequestDTO.getOrderId())
            .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        /* validate merch variant item exists and acquire pessimistic write lock to prevent concurrent stock updates */
        MerchVariantItem merchVariantItem = merchVariantItemRepository.findByIdWithLock(orderItemRequestDTO.getMerchVariantItemId())
            .orElseThrow(() -> new InvalidRequestException("MerchVariantItem not found"));
        
        /* extract merchVariantId from MerchVariantItem */
        
        /* validate quantity */
        if (orderItemRequestDTO.getQuantity() == null || orderItemRequestDTO.getQuantity() <= 0) {
            throw new InvalidRequestException("Quantity must be greater than 0");
        }
        
        /* validate sufficient stock */
        if (orderItemRequestDTO.getQuantity() > merchVariantItem.getStockQuantity()) {
            throw new InvalidRequestException("Insufficient stock. Available: " + merchVariantItem.getStockQuantity() + 
                    ", Requested: " + orderItemRequestDTO.getQuantity());
        }
        
        /* validate price */
        if (orderItemRequestDTO.getPriceAtPurchase() == null || orderItemRequestDTO.getPriceAtPurchase() < 0) {
            throw new InvalidRequestException("Price at purchase must be non-negative");
        }
        
        try {
            /* create order item with stock deduction to reserve inventory */
            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .merchVariantItem(merchVariantItem)
                .quantity(orderItemRequestDTO.getQuantity())
                .priceAtPurchase(orderItemRequestDTO.getPriceAtPurchase())
                .updatedAt(LocalDateTime.now())
                .build();
            
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            
            /* deduct stock from MerchVariantItem (pessimistic lock already held) */
            /* stock is reserved when order item is created */
            int newStockQuantity = merchVariantItem.getStockQuantity() - orderItemRequestDTO.getQuantity();
            merchVariantItem.setStockQuantity(newStockQuantity);
            merchVariantItemRepository.save(merchVariantItem);
            
            System.out.println("Order item created successfully. Stock deducted: " + orderItemRequestDTO.getQuantity());
            return orderItemMapper.toResponseDTO(savedOrderItem);
        } catch (Exception e) {
            /* log error and rethrow to trigger transaction rollback */
            System.err.println("Error creating order item and deducting stock: " + e.getMessage());
            e.printStackTrace();
            throw new InvalidRequestException("Failed to create order item: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to extract MerchVariantId from MerchVariantItem
     */
    private Long getMerchVariantIdFromItem(MerchVariantItem merchVariantItem) {
        if (merchVariantItem == null) {
            throw new InvalidRequestException("MerchVariantItem is null");
        }
        if (merchVariantItem.getMerchVariant() == null) {
            throw new InvalidRequestException("MerchVariant is null for MerchVariantItem");
        }
        return merchVariantItem.getMerchVariant().getMerchVariantId();
    }
    
    @Override
    public OrderItemResponseDTO getOrderItemById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("Invalid order item ID");
        }
        
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException("Order item not found"));
        
        return orderItemMapper.toResponseDTO(orderItem);
    }
    
    @Override
    public Page<OrderItemResponseDTO> getOrderItemsByStatus(OrderStatus status, Pageable pageable, String studentId) {
        if (status == null) {
            throw new InvalidRequestException("Order status is required");
        }
        
        if (studentId == null || studentId.isEmpty()) {
            throw new InvalidRequestException("Student ID is required");
        }
        
        Page<OrderItem> orderItems = orderItemRepository.findByOrderStatusAndOrderStudentStudentId(status, studentId, pageable);
        return orderItems.map(orderItemMapper::toResponseDTO);

    }

    @Override
    public List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new InvalidRequestException("Invalid order ID");
        }
        
        // Verify order exists
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found");
        }
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
        return orderItems.stream()
            .map(orderItemMapper::toResponseDTO)
            .toList();
    }
    
    @Override
    public Page<OrderItemResponseDTO> getOrderItemsByOrderIdPaginated(Long orderId, Pageable pageable) {
        if (orderId == null || orderId <= 0) {
            throw new InvalidRequestException("Invalid order ID");
        }
        
        // Verify order exists
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found");
        }
        
        Page<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId, pageable);
        return orderItems.map(orderItemMapper::toResponseDTO);
    }
    
    @Override
    public Page<OrderItemResponseDTO> getOrderItemsByStudentIdPaginated(String studentId, Pageable pageable) {
        if (studentId == null || studentId.isEmpty()) {
            throw new InvalidRequestException("Student ID is required");
        }
        
        Page<OrderItem> orderItems = orderItemRepository.findByOrderStudentStudentIdOrderByUpdatedAtDesc(studentId, pageable);
        return orderItems.map(orderItemMapper::toResponseDTO);
    }

    @Override
    public Page<OrderItemResponseDTO> getOrderItemsByStudentIdAndStatusPaginated(String studentId, OrderStatus status, Pageable pageable) {
        if (studentId == null || studentId.isEmpty()) {
            throw new InvalidRequestException("Student ID is required");
        }
        
        if (status == null) {
            throw new InvalidRequestException("Order status is required");
        }
        
        Page<OrderItem> orderItems = orderItemRepository.findByOrderStudentStudentIdAndOrderStatusOrderByUpdatedAtDesc(studentId, status, pageable);
        return orderItems.map(orderItemMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public OrderItemResponseDTO updateOrderItemStatus(Long id, OrderStatus status) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("Invalid order item ID");
        }
        
        if (status == null) {
            throw new InvalidRequestException("Order status is required");
        }
        
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException("Order item not found"));
        
        OrderStatus oldStatus = orderItem.getOrderStatus();
        
        try {
            /* restore stock only when transitioning TO REJECTED status (prevent duplicate restorations) */
            if (status == OrderStatus.REJECTED && oldStatus != OrderStatus.REJECTED) {
                /* acquire pessimistic lock on merch variant item to ensure thread-safe stock restoration */
                MerchVariantItem merchVariantItem = merchVariantItemRepository.findByIdWithLock(orderItem.getMerchVariantItem().getMerchVariantItemId())
                    .orElseThrow(() -> new InvalidRequestException("MerchVariantItem not found during stock restoration"));
                
                /* restore stock when order is rejected */
                int restoredStockQuantity = merchVariantItem.getStockQuantity() + orderItem.getQuantity();
                merchVariantItem.setStockQuantity(restoredStockQuantity);
                merchVariantItemRepository.save(merchVariantItem);
                
                System.out.println("Stock restored due to order rejection. Quantity: " + orderItem.getQuantity() + 
                    " | Previous status: " + oldStatus + " -> New status: " + status);
            }
            
            /* update order item status */
            orderItem.setOrderStatus(status);
            orderItem.setUpdatedAt(LocalDateTime.now());
            
            OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

            /* send notification email if order details are available */
            OrderItem itemWithDetails = orderItemRepository.findByIdWithStudentAndMerchDetails(id)
                .orElse(null);
            if (itemWithDetails != null) {
                var notificationData = orderNotificationService.extractNotificationData(itemWithDetails, status);
                if (notificationData != null) {
                    orderNotificationService.sendOrderStatusEmail(notificationData);
                }
            }

            return orderItemMapper.toResponseDTO(updatedOrderItem);
        } catch (InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            /* log error and rethrow to trigger transaction rollback */
            System.err.println("Error updating order item status: " + e.getMessage());
            e.printStackTrace();
            throw new InvalidRequestException("Failed to update order item status: " + e.getMessage());
        }
    }


    @Override
    @Transactional
    public void deleteOrderItem(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("Invalid order item ID");
        }
        
        if (!orderItemRepository.existsById(id)) {
            throw new OrderItemNotFoundException("Order item not found");
        }
        
        orderItemRepository.deleteById(id);
    }
}
