package org.csps.backend.service.impl;

import org.csps.backend.domain.dtos.OrderNotificationDTO;
import org.csps.backend.domain.entities.MerchVariantItem;
import org.csps.backend.domain.entities.OrderItem;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.csps.backend.domain.enums.OrderStatus;
import org.csps.backend.service.EmailService;
import org.csps.backend.service.OrderNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationServiceImpl implements OrderNotificationService {

    private final EmailService emailService;

    @Value("${S3_PUBLIC_BASE_URL:https://csps-web.s3.us-east-1.amazonaws.com/}")
    private String s3PublicBaseUrl;

    /**
     * extracts notification data from the order item synchronously within the transaction.
     * performs all lazy-load access here while the session is still active.
     * returns null if user is not eligible for notifications.
     *
     * @param orderItem the order item with eagerly loaded relationships
     * @param newStatus the new order status
     * @return the extracted notification data, or null if not eligible
     */
    @Override
    public OrderNotificationDTO extractNotificationData(OrderItem orderItem, OrderStatus newStatus) {
        try {
            // extract student email info from the relationship chain (done synchronously within transaction)
            Student student = orderItem.getOrder().getStudent();
            UserAccount userAccount = student.getUserAccount();
            UserProfile userProfile = userAccount.getUserProfile();

            // check eligibility while relationships are still accessible
            if (!isEligibleForNotification(userAccount, userProfile)) {
                log.debug("skipping notification for order item {} - user not eligible", orderItem.getOrderItemId());
                return null;
            }

            String email = userProfile.getEmail();
            String studentName = userProfile.getFirstName() + " " + userProfile.getLastName();
            String merchName = extractMerchName(orderItem);
            String s3ImageKey = orderItem.getMerchVariantItem().getMerchVariant().getS3ImageKey();
            Long orderItemId = orderItem.getOrderItemId();
            Long orderId = orderItem.getOrder().getOrderId();

            return OrderNotificationDTO.builder()
                    .orderItemId(orderItemId)
                    .orderId(orderId)
                    .studentEmail(email)
                    .studentName(studentName)
                    .merchName(merchName)
                    .s3ImageKey(s3ImageKey)
                    .newStatus(newStatus)
                    .build();
        } catch (Exception e) {
            log.error("failed to extract notification data for order item {}: {}", orderItem.getOrderItemId(), e.getMessage());
            return null;
        }
    }

    /**
     * sends an email notification asynchronously using pre-extracted data.
     * only sends for REJECTED and TO_BE_CLAIMED statuses.
     * no lazy-loading occurs here - only pure data is used.
     *
     * @param notificationData the extracted notification data
     */
    @Override
    @Async("emailTaskExecutor")
    public void sendOrderStatusEmail(OrderNotificationDTO notificationData) {
        if (notificationData == null) {
            return;
        }

        try {
            switch (notificationData.getNewStatus()) {
                case REJECTED -> sendRejectionEmail(notificationData);
                case TO_BE_CLAIMED -> sendReadyToClaimEmail(notificationData);
                default -> log.debug("no email notification for status: {}", notificationData.getNewStatus());
            }
        } catch (Exception e) {
            // log but don't rethrow - email failure should not affect anything
            log.error("failed to send order notification email for order item {}: {}", notificationData.getOrderItemId(), e.getMessage());
        }
    }

    /**
     * checks if the user is eligible to receive email notifications.
     *
     * @param userAccount the user's account (for verification status)
     * @param userProfile the user's profile (for email address)
     * @return true if the user is verified and has a valid email
     */
    private boolean isEligibleForNotification(UserAccount userAccount, UserProfile userProfile) {
        if (userAccount == null || userProfile == null) {
            return false;
        }
        return Boolean.TRUE.equals(userAccount.getIsVerified())
                && userProfile.getEmail() != null
                && !userProfile.getEmail().isBlank();
    }

    /**
     * extracts the merchandise name from the order item's relationship chain.
     *
     * @param orderItem the order item
     * @return the merch name with variant details
     */
    private String extractMerchName(OrderItem orderItem) {
        MerchVariantItem variantItem = orderItem.getMerchVariantItem();
        String merchName = variantItem.getMerchVariant().getMerch().getMerchName();
        String color = variantItem.getMerchVariant().getColor();
        String size = variantItem.getSize() != null ? variantItem.getSize().name() : null;

        StringBuilder name = new StringBuilder(merchName);
        if (color != null && !color.isBlank()) {
            name.append(" - ").append(color);
        }
        if (size != null) {
            name.append(" (").append(size).append(")");
        }
        return name.toString();
    }

    /**
     * sends a rejection notification email using extracted data.
     */
    private void sendRejectionEmail(OrderNotificationDTO data) {
        String subject = "Order Item Rejected - CSPS Store";
        String htmlBody = buildOrderStatusEmailTemplate(
                data.getStudentName(),
                data.getMerchName(),
                buildImageUrl(data.getS3ImageKey()),
                data.getOrderItemId(),
                "Order Rejected",
                "We're sorry, but your order item has been <strong style=\"color: #dc2626;\">rejected</strong>.",
                "If you believe this was a mistake or have questions, please contact the CSPS administrators.",
                "#dc2626"
        );
        emailService.sendHtmlEmail(data.getStudentEmail(), subject, htmlBody);
    }

    /**
     * sends a ready-to-claim notification email using extracted data.
     */
    private void sendReadyToClaimEmail(OrderNotificationDTO data) {
        String subject = "Order Item Ready to Claim - CSPS Store";
        String htmlBody = buildOrderStatusEmailTemplate(
                data.getStudentName(),
                data.getMerchName(),
                buildImageUrl(data.getS3ImageKey()),
                data.getOrderItemId(),
                "Ready to Claim",
                "Great news! Your order item has been <strong style=\"color: #16a34a;\">accepted</strong> and is now ready for pickup.",
                "Please claim your item at the designated pick-up area. Don't forget to bring your student ID.",
                "#16a34a"
        );
        emailService.sendHtmlEmail(data.getStudentEmail(), subject, htmlBody);
    }

    /**
     * builds a styled HTML email template for order status notifications.
     *
     * @param studentName the student's full name
     * @param merchName the merchandise name with variant details
     * @param orderItemId the order item identifier
     * @param statusLabel the status label to display
     * @param statusMessage the main status message body
     * @param actionMessage the call-to-action or follow-up message
     * @param accentColor hex color for the status accent
     * @return the formatted HTML email string
     */
    private String buildOrderStatusEmailTemplate(
            String studentName,
            String merchName,
            String productImageUrl,
            Long orderItemId,
            String statusLabel,
            String statusMessage,
            String actionMessage,
            String accentColor) {
        String productSection = buildProductSection(productImageUrl, merchName);

        return """
            <html>
            <body style="margin: 0; padding: 0; background-color: #f4f2f7; font-family: 'Segoe UI', Arial, sans-serif;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f2f7; padding: 40px 20px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.06);">

                                <!-- accent bar -->
                                <tr>
                                    <td style="height: 4px; background: linear-gradient(90deg, #7c3aed, %s);"></td>
                                </tr>

                                <!-- content -->
                                <tr>
                                    <td style="padding: 40px;">

                                        <h1 style="margin: 0 0 8px; font-size: 22px; font-weight: 700; color: #1a1a2e; letter-spacing: -0.02em;">
                                            Order Status Update
                                        </h1>

                                        <p style="margin: 0 0 20px; font-size: 14px; color: #6b7280; line-height: 1.6;">
                                            Hello <strong style="color: #1a1a2e;">%s</strong>,
                                        </p>

                                        <p style="margin: 0 0 24px; font-size: 14px; color: #6b7280; line-height: 1.6;">
                                            %s
                                        </p>

                                        %s

                                        <!-- order details card -->
                                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin: 0 0 24px;">
                                            <tr>
                                                <td style="background-color: #faf8ff; border: 1px solid #ede9fe; border-radius: 10px; padding: 20px;">
                                                    <p style="margin: 0 0 12px; font-size: 12px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.08em; color: #7c3aed;">
                                                        Order Details
                                                    </p>
                                                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td style="padding: 4px 0; font-size: 13px; color: #6b7280;">Item</td>
                                                            <td style="padding: 4px 0; font-size: 13px; color: #1a1a2e; font-weight: 600; text-align: right;">%s</td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 4px 0; font-size: 13px; color: #6b7280;">Order Item #</td>
                                                            <td style="padding: 4px 0; font-size: 13px; color: #1a1a2e; font-weight: 600; text-align: right;">%d</td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 4px 0; font-size: 13px; color: #6b7280;">Status</td>
                                                            <td style="padding: 4px 0; font-size: 13px; font-weight: 700; text-align: right; color: %s;">%s</td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>

                                        <p style="margin: 0 0 28px; font-size: 13px; color: #6b7280; line-height: 1.6;">
                                            %s
                                        </p>

                                        <hr style="border: none; border-top: 1px solid #f0ecf9; margin: 0 0 24px;" />

                                        <p style="margin: 0; font-size: 12px; color: #9ca3af; line-height: 1.5;">
                                            This is an automated notification from CSPS Store. Please do not reply to this email.
                                        </p>
                                    </td>
                                </tr>
                            </table>

                            <p style="margin: 24px 0 0; font-size: 11px; color: #9ca3af; letter-spacing: 0.05em;">
                                CSPS &mdash; Computer Studies and Programming Society
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(accentColor, studentName, statusMessage, productSection, merchName, orderItemId, accentColor, statusLabel, actionMessage);
    }

    private String buildImageUrl(String s3ImageKey) {
        if (s3ImageKey == null || s3ImageKey.isBlank()) {
            return "";
        }
        if (s3ImageKey.startsWith("http://") || s3ImageKey.startsWith("https://")) {
            return s3ImageKey;
        }
        String base = s3PublicBaseUrl.endsWith("/") ? s3PublicBaseUrl : s3PublicBaseUrl + "/";
        return base + s3ImageKey;
    }

    private String buildProductSection(String imageUrl, String merchName) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return """
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin: 0 0 16px;">
                    <tr>
                        <td align="center" style="font-size: 14px; color: #1a1a2e; font-weight: 600;">
                            %s
                        </td>
                    </tr>
                </table>
                """.formatted(merchName);
        }

        return """
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin: 0 0 24px;">
                <tr>
                    <td align="center" style="padding: 0 0 12px;">
                        <img src="%s" alt="%s" style="max-width: 120px; height: auto; border-radius: 8px; border: 1px solid #ede9fe;" />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="font-size: 14px; color: #1a1a2e; font-weight: 600;">
                        %s
                    </td>
                </tr>
            </table>
            """.formatted(imageUrl, merchName, merchName);
    }
}
