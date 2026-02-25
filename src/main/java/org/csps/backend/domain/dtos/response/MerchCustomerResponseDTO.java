package org.csps.backend.domain.dtos.response;

import java.time.LocalDateTime;

import org.csps.backend.domain.enums.ClothingSizing;
import org.csps.backend.domain.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO representing a customer who purchased a specific merch.
 * Combines student profile information with order details for admin views.
 *
 * @field studentId      the student's unique 8-character ID
 * @field studentName    full name (firstName + lastName) from UserProfile
 * @field yearLevel      the student's current year level
 * @field merchName      the name of the purchased merch
 * @field color          the variant color (nullable for non-clothing)
 * @field design         the variant design
 * @field size           the item size (nullable for non-clothing)
 * @field quantity       the number of items purchased
 * @field totalPrice     quantity * priceAtPurchase
 * @field orderStatus    current status of the order item (PENDING, CLAIMED, etc.)
 * @field orderDate      when the order was placed
 * @field s3ImageKey     S3 key for the variant image
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchCustomerResponseDTO {

    private String studentId;

    private String studentName;

    private Byte yearLevel;

    private String merchName;

    private String color;

    private String design;

    private ClothingSizing size;

    private Integer quantity;

    private Double totalPrice;

    private OrderStatus orderStatus;

    private LocalDateTime orderDate;

    private String s3ImageKey;
}
