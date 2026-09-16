package com.digipals.wms.finance.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryValuationResponse(
        UUID warehouseId,
        String warehouseCode,
        UUID productId,
        String sku,
        String productName,
        BigDecimal quantityOnHand,
        BigDecimal unitCost,
        BigDecimal inventoryValue
) {}
