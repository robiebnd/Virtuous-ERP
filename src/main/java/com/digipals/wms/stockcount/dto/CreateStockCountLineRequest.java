package com.digipals.wms.stockcount.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateStockCountLineRequest {

    @NotNull(message = "Stock Count is required")
    private UUID stockCountId;

    @NotNull(message = "Product is required")
    private UUID productId;

    @NotNull(message = "Bin is required")
    private UUID binId;

    @NotNull(message = "Counted quantity is required")
    @DecimalMin(value = "0.00")
    private BigDecimal countedQuantity;

    private String reason;
}