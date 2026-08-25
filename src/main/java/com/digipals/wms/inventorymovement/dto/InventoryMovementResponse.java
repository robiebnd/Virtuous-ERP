package com.digipals.wms.inventorymovement.dto;

import com.digipals.wms.inventorymovement.entity.InventoryMovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryMovementResponse(
        UUID id,
        LocalDateTime movementDate,
        InventoryMovementType movementType,
        String referenceType,
        UUID referenceId,
        String referenceNumber,
        UUID warehouseId,
        UUID fromBinId,
        UUID toBinId,
        UUID productId,
        String sku,
        BigDecimal quantity,
        UUID performedById,
        String remarks,
        LocalDateTime createdAt
) {}
