package org.csps.backend.domain.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for batch-creating orders for multiple students who paid for a specific merch.
 * Each entry in the list includes a studentId and the actual orderDate,
 * so the real purchase timestamp is preserved (not overwritten by server time).
 *
 * @field entries              list of student+date pairs representing individual payments
 * @field merchVariantItemId   the specific MerchVariantItem (SKU) they purchased
 * @field quantity             quantity per student, defaults to 1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkMerchPaymentRequestDTO {

    @NotEmpty(message = "At least one payment entry is required")
    @Valid
    private List<BulkPaymentEntryDTO> entries;

    @NotNull(message = "Merch variant item ID is required")
    private Long merchVariantItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Builder.Default
    private Integer quantity = 1;
}
