package com.digipals.wms.inventory.reconciliation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InventoryReconciliationRequest {

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    @NotNull(message = "Bin is required")
    private UUID binId;

    @NotNull(message = "Product is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    private BigDecimal quantity;

    @NotBlank(message = "Reference number is required")
    private String referenceNumber;

    private String remarks;
}
