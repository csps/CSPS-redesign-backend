package org.csps.backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.csps.backend.domain.dtos.response.sales.ChartPointDTO;
import org.csps.backend.domain.dtos.response.sales.SalesStatsDTO;
import org.csps.backend.domain.dtos.response.sales.TransactionDTO;
import org.csps.backend.domain.entities.Order;
import org.csps.backend.domain.entities.OrderItem;
import org.csps.backend.domain.enums.OrderStatus;
import org.csps.backend.domain.enums.SalesPeriod;
import org.csps.backend.repository.MerchVariantItemRepository;
import org.csps.backend.repository.OrderItemRepository;
import org.csps.backend.repository.OrderRepository;
import org.csps.backend.service.SalesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final OrderRepository orderRepository;
    private final MerchVariantItemRepository merchVariantItemRepository;

    private final OrderItemRepository orderItemRepository;

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
    public Page<TransactionDTO> getTransactions(Pageable pageable, String search, String status, Integer year) {
        List<Order> allOrders = orderRepository.findAll();

        List<TransactionDTO> filteredOrders = allOrders.stream()
                .filter(order -> {
                    // Filter by status
                    if (status != null && !status.isEmpty()) {
                        String orderStatusStr = order.getOrderStatus().name();
                        if (!orderStatusStr.equals(status)) {
                            return false;
                        }
                    }

                    // Filter by year
                    if (year != null && order.getOrderDate() != null) {
                        if (order.getOrderDate().getYear() != year) {
                            return false;
                        }
                    }

                    // Filter by search
                    if (search != null && !search.isEmpty()) {
                        String searchLower = search.toLowerCase();
                        String studentName = order.getStudent().getUserAccount().getUserProfile().getFirstName() + " " +
                                order.getStudent().getUserAccount().getUserProfile().getLastName();
                        if (!studentName.toLowerCase().contains(searchLower) &&
                                !order.getOrderId().toString().contains(searchLower)) {
                            return false;
                        }
                    }

                    return true;
                })
                .map(this::mapToTransactionDTO)
                .sorted(Comparator.comparing(TransactionDTO::getOrderId))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredOrders.size());

        List<TransactionDTO> pageContent = filteredOrders.subList(start, end);
        return new PageImpl<>(pageContent, pageable, filteredOrders.size());
    }

    @Override
    @Transactional
    public TransactionDTO approveTransaction(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

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