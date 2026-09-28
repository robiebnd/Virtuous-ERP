package com.digipals.wms.integration.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record StockAdjustmentPostedEvent(
        UUID sourceDocumentId,
        String sourceDocumentNumber,
        String currency,
        BigDecimal inventoryIncreaseAmount,
        BigDecimal inventoryDecreaseAmount,
        LocalDateTime postingDate
) {}
