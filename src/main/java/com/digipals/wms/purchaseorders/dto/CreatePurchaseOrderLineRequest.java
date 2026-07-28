package com.digipals.wms.purchaseorders.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreatePurchaseOrderLineRequest {

    @NotNull(message = "Purchase Order is required")
    private UUID purchaseOrderId;

    @NotNull(message = "Product is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01")
    private BigDecimal quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.00")
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    private BigDecimal tax = BigDecimal.ZERO;

    private String remarks;
}