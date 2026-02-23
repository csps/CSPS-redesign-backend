package org.csps.backend.service;

import org.csps.backend.domain.dtos.OrderNotificationDTO;
import org.csps.backend.domain.entities.OrderItem;
import org.csps.backend.domain.enums.OrderStatus;

public interface OrderNotificationService {

    /**
     * extracts notification data from the order item synchronously (within transaction).
     * checks if the user is eligible for notifications.
     * returns the extracted data if eligible, null otherwise.
     * this method must be called within an active transaction to access relationships.
     *
     * @param orderItem the order item with eagerly loaded student and merch data
     * @param newStatus the new status that triggered the notification
     * @return the extracted notification data, or null if user is not eligible
     */
    OrderNotificationDTO extractNotificationData(OrderItem orderItem, OrderStatus newStatus);

    /**
     * sends an email notification asynchronously.
     * only sends for REJECTED and TO_BE_CLAIMED statuses.
     * uses extracted DTO data - safe to run outside transaction context.
     *
     * @param notificationData the extracted notification data (no entity references)
     */
    void sendOrderStatusEmail(OrderNotificationDTO notificationData);
}
