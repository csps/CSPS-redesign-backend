package org.csps.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.csps.backend.domain.dtos.request.OrderItemRequestDTO;
import org.csps.backend.domain.dtos.response.OrderItemResponseDTO;
import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.entities.OrderItem;
import org.csps.backend.domain.entities.MerchVariantItem;
import org.csps.backend.domain.enums.OrderStatus;
import org.csps.backend.exception.InvalidRequestException;
import org.csps.backend.exception.OrderItemNotFoundException;
import org.csps.backend.exception.OrderNotFoundException;
import org.csps.backend.mapper.OrderItemMapper;
import org.csps.backend.repository.OrderItemRepository;
import org.csps.backend.repository.OrderRepository;
import org.csps.backend.repository.MerchVariantItemRepository;
import org.csps.backend.service.OrderItemService;
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
    
    @Override
    @Transactional
    public OrderItemResponseDTO createOrderItem(OrderItemRequestDTO orderItemRequestDTO) {
        if (orderItemRequestDTO == null) {
            throw new InvalidRequestException("Order item request is required");
        }
        
        // Validate order exists
        Order order = orderRepository.findById(orderItemRequestDTO.getOrderId())
            .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        // Validate merch variant item exists
        MerchVariantItem merchVariantItem = merchVariantItemRepository.findById(orderItemRequestDTO.getMerchVariantItemId())
            .orElseThrow(() -> new InvalidRequestException("MerchVariantItem not found"));
        
        // Extract merchVariantId from MerchVariantItem
        Long merchVariantId = getMerchVariantIdFromItem(merchVariantItem);
        
        // Validate quantity
        if (orderItemRequestDTO.getQuantity() == null || orderItemRequestDTO.getQuantity() <= 0) {
            throw new InvalidRequestException("Quantity must be greater than 0");
        }
        
        // Validate sufficient stock
        if (orderItemRequestDTO.getQuantity() > merchVariantItem.getStockQuantity()) {
            throw new InvalidRequestException("Insufficient stock. Available: " + merchVariantItem.getStockQuantity() + 
                    ", Requested: " + orderItemRequestDTO.getQuantity());
        }
        
        // Validate price
        if (orderItemRequestDTO.getPriceAtPurchase() == null || orderItemRequestDTO.getPriceAtPurchase() < 0) {
            throw new InvalidRequestException("Price at purchase must be non-negative");
        }
        
        // Create order item
        OrderItem orderItem = OrderItem.builder()
            .order(order)
            .merchVariantItem(merchVariantItem)
            .quantity(orderItemRequestDTO.getQuantity())
            .priceAtPurchase(orderItemRequestDTO.getPriceAtPurchase())
            .updatedAt(LocalDateTime.now())
            .build();
        
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        
        // Deduct stock from MerchVariantItem
        int newStockQuantity = merchVariantItem.getStockQuantity() - orderItemRequestDTO.getQuantity();
        merchVariantItem.setStockQuantity(newStockQuantity);
        merchVariantItemRepository.save(merchVariantItem);
        
        return orderItemMapper.toResponseDTO(savedOrderItem);
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
    public Page<OrderItemResponseDTO> getOrderItemsByStatus(OrderStatus status, Pageable pageable) {
        if (status == null) {
            throw new InvalidRequestException("Order status is required");
        }
        

        Page<OrderItem> orderItems = orderItemRepository.findByOrderStatus(status,pageable);


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
    @Transactional
    public OrderItemResponseDTO updateOrderItemStatus(Long id, OrderItemRequestDTO orderItemRequestDTO) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("Invalid order item ID");
        }
        
        if (orderItemRequestDTO == null || orderItemRequestDTO.getOrderStatus() == null) {
            throw new InvalidRequestException("Order status is required");
        }
        
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException("Order item not found"));
        
        orderItem.setOrderStatus(orderItemRequestDTO.getOrderStatus());
        orderItem.setUpdatedAt(LocalDateTime.now());
        
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return orderItemMapper.toResponseDTO(updatedOrderItem);
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
