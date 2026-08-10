package com.digipals.wms.purchaseorders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePurchaseOrderRequest {

    @NotNull(message = "Supplier is required")
    private UUID supplierId;
}
