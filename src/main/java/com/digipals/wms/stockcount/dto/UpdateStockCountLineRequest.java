package com.digipals.wms.stockcount.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateStockCountLineRequest {

    @NotNull(message = "Counted quantity is required")
    @DecimalMin(value = "0.00", message = "Counted quantity cannot be negative")
    private BigDecimal countedQuantity;

    private String reason;
}
