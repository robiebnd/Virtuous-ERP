package com.digipals.wms.purchaserequisition.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreatePurchaseRequisitionLineRequest {

    @NotNull(message = "Product is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(
            value = "0.01",
            message = "Quantity must be greater than zero"
    )
    private BigDecimal quantity;

    @DecimalMin(
            value = "0.00",
            message = "Estimated unit cost cannot be negative"
    )
    private BigDecimal estimatedUnitCost;

    private String remarks;
}
