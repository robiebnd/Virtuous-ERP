package com.digipals.wms.finance.dto;

import java.math.BigDecimal;

public record InventoryGlReconciliationResponse(
        BigDecimal inventoryValuation,
        BigDecimal inventoryGlBalance,
        BigDecimal variance,
        boolean balanced
) {}
