package com.digipals.wms.inventorymovement.dto;

import com.digipals.wms.inventorymovement.entity.InventoryMovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryMovementResponse(
        LocalDateTime movementDate,
        InventoryMovementType movementType,
        String referenceType,
        String referenceNumber,
        String warehouseCode,
        String fromBinCode,
        String toBinCode,
        String sku,
        String productName,
        BigDecimal quantity,
        String performedBy,
        String remarks,
        LocalDateTime createdAt
) {}
