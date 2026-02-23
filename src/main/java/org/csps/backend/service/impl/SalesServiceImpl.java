package org.csps.backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.csps.backend.domain.dtos.request.OrderSearchDTO;
import org.csps.backend.domain.dtos.request.StudentMembershipRequestDTO;
import org.csps.backend.domain.dtos.response.sales.ChartPointDTO;
import org.csps.backend.domain.dtos.response.sales.SalesStatsDTO;
import org.csps.backend.domain.dtos.response.sales.TransactionDTO;
import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.entities.OrderItem;
import org.csps.backend.domain.enums.MerchType;
import org.csps.backend.domain.enums.OrderStatus;
import org.csps.backend.domain.enums.SalesPeriod;
import org.csps.backend.repository.MerchVariantItemRepository;
import org.csps.backend.repository.OrderItemRepository;
import org.csps.backend.repository.OrderRepository;
import org.csps.backend.repository.specification.OrderSpecification;
import org.csps.backend.service.SalesService;
import org.csps.backend.service.StudentMembershipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final OrderRepository orderRepository;
    private final MerchVariantItemRepository merchVariantItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final StudentMembershipService studentMembershipService;
    
    private final OrderSpecification orderSpecification;


    // Deployment date - set to today (February 8, 2026) as dummy data
    private static final LocalDate DEPLOYMENT_DATE = LocalDate.of(2026, 2, 8);

    @Override
    public SalesStatsDTO getSalesStats(SalesPeriod period) {
        List<Order> claimedOrders = orderRepository.findByOrderStatus(OrderStatus.CLAIMED);

        List<ChartPointDTO> chartData = generateChartData(claimedOrders, period);

        // Calculate total sales from chart data (sum of all chart values)
        BigDecimal totalSales = chartData.stream()
                .map(ChartPointDTO::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SalesStatsDTO.builder()
                .totalSales(totalSales)
                .currency("PHP")
                .chartData(chartData)
                .build();
    }

    @Override
    public Page<TransactionDTO> getTransactions(Pageable pageable, OrderSearchDTO searchDTO) {
        /* build specification for database-level filtering to prevent loading all orders into memory */
        Specification<Order> spec = orderSpecification.withFilters(searchDTO);
        
        /* fetch paginated results with eager loading via @EntityGraph in OrderRepository.findAll(pageable) */
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        
        /* map orders to DTOs */
        return orders.map(this::mapToTransactionDTO);
    }



    @Override
    @Transactional
    public TransactionDTO approveTransaction(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check for MEMBERSHIP items and create membership if found
        if (order.getOrderItems() != null) {
            boolean hasMembership = false;
            for (OrderItem item : order.getOrderItems()) {
                if (item.getMerchVariantItem() != null 
                        && item.getMerchVariantItem().getMerchVariant() != null
                        && item.getMerchVariantItem().getMerchVariant().getMerch() != null
                        && item.getMerchVariantItem().getMerchVariant().getMerch().getMerchType() == MerchType.MEMBERSHIP) {
                    hasMembership = true;
                    break;
                }
            }

            if (hasMembership) {
                try {
                    StudentMembershipRequestDTO membershipRequest = StudentMembershipRequestDTO.builder()
                            .studentId(order.getStudent().getStudentId())
                            .academicYear(order.getStudent().getYearLevel())
                            .semester((byte) 2) // Default semester
                            .active(true)
                            .build();
                    
                    studentMembershipService.createStudentMembership(membershipRequest);
                } catch (Exception e) {
                    // Log error but proceed with order approval? Or fail?
                    // For now, let's log and proceed, or maybe rethrow if strict
                    System.err.println("Failed to create membership for order " + id + ": " + e.getMessage());
                    // Decide: Should we fail the transaction if membership fails? 
                    // Probably yes, to ensure consistency.
                    throw new RuntimeException("Failed to create membership: " + e.getMessage(), e);
                }
            }
        }

        order.setOrderStatus(OrderStatus.CLAIMED);
        Order savedOrder = orderRepository.save(order);

        return mapToTransactionDTO(savedOrder);
    }

    @Override
    @Transactional
    public void rejectTransaction(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Restore inventory for all order items in this order and reject them
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                // Restore the quantity to the MerchVariantItem
                var merchVariantItem = orderItem.getMerchVariantItem();
                if (merchVariantItem != null) {
                    Integer currentStock = merchVariantItem.getStockQuantity();
                    int restoredStock = currentStock + orderItem.getQuantity();
                    merchVariantItem.setStockQuantity(restoredStock);
                    merchVariantItemRepository.save(merchVariantItem);
                }
                
                // Set order item status to REJECTED
                orderItem.setOrderStatus(OrderStatus.REJECTED);
                orderItem.setUpdatedAt(LocalDateTime.now());
                orderItemRepository.save(orderItem);
            }
        }

        order.setOrderStatus(OrderStatus.REJECTED);
        orderRepository.save(order);
    }

    private TransactionDTO mapToTransactionDTO(Order order) {
        String studentName = order.getStudent().getUserAccount().getUserProfile().getFirstName() + " " +
                order.getStudent().getUserAccount().getUserProfile().getLastName();

        return TransactionDTO.builder()
                .id(order.getOrderId())
                .orderId(order.getOrderId())
                .studentId(order.getStudent().getStudentId())
                .studentName(studentName)
                .idNumber(order.getStudent().getStudentId())
                .membershipType("Member") // Can be enhanced with actual membership data
                .amount(BigDecimal.valueOf(order.getTotalPrice()))
                .date(order.getOrderDate().toLocalDate().toString())
                .status(order.getOrderStatus().name())
                .build();
    }

    private List<ChartPointDTO> generateChartData(List<Order> orders, SalesPeriod period) {
        Map<String, BigDecimal> groupedData = new LinkedHashMap<>();

        orders.forEach(order -> {
            if (order.getOrderDate() == null || order.getTotalPrice() == null) {
                return;
            }

            LocalDate orderDate = order.getOrderDate().toLocalDate();
            String key;

            switch (period) {
                case DAILY:
                    key = orderDate.toString();
                    break;
                case WEEKLY:
                    // Calculate weeks since deployment date
                    long daysSinceDeployment = java.time.temporal.ChronoUnit.DAYS.between(DEPLOYMENT_DATE, orderDate);
                    int weekSinceDeployment = (int) Math.ceil((daysSinceDeployment + 1.0) / 7.0);
                    key = "Week " + Math.max(1, weekSinceDeployment);
                    break;
                case MONTHLY:
                    key = orderDate.getMonth().toString() + " " + orderDate.getYear();
                    break;
                case YEARLY:
                    key = String.valueOf(orderDate.getYear());
                    break;
                case ALL_TIME:
                    key = "All Time";
                    break;
                default:
                    key = orderDate.toString();
            }

            groupedData.put(key, groupedData.getOrDefault(key, BigDecimal.ZERO)
                    .add(BigDecimal.valueOf(order.getTotalPrice())));
        });

        return groupedData.entrySet().stream()
                .map(entry -> ChartPointDTO.builder()
                        .label(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}