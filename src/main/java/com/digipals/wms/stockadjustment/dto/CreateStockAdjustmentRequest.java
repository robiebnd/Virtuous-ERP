package com.digipals.wms.stockadjustment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateStockAdjustmentRequest {

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    private String reason;

    private String remarks;
}