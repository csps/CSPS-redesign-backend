package org.csps.backend.domain.dtos;

import org.csps.backend.domain.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for email notification data.
 * contains all extracted information needed to send order status notifications.
 * allows async email sending without lazy-loading issues.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDTO {
    private Long orderItemId;
    private Long orderId;
    private String studentEmail;
    private String studentName;
    private String merchName;
    private String s3ImageKey;
    private OrderStatus newStatus;
}
