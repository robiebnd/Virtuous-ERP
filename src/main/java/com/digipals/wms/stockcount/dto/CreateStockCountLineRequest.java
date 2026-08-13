package com.digipals.wms.stockcount.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateStockCountLineRequest {

    private UUID stockCountId;

    private UUID productId;

    private UUID binId;

    @NotNull(message = "Counted quantity is required")
    @DecimalMin(value = "0.00", message = "Counted quantity cannot be negative")
    private BigDecimal countedQuantity;

    private String reason;
}
