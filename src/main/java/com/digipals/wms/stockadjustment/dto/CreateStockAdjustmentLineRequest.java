package com.digipals.wms.stockadjustment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateStockAdjustmentLineRequest {

    @NotNull(message = "Adjustment is required")
    private UUID stockAdjustmentId;

    @NotNull(message = "Product is required")
    private UUID productId;

    @NotNull(message = "Counted quantity is required")
    @DecimalMin(value = "0.00")
    private BigDecimal countedQuantity;

    private String reason;
}