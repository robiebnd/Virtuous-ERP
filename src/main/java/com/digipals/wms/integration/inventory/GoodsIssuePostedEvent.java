package com.digipals.wms.integration.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Integration contract published when Inventory/O2C posts a customer goods issue.
 * Finance consumes the event to recognize COGS and inventory consumption.
 */
public record GoodsIssuePostedEvent(
        UUID sourceDocumentId,
        String sourceDocumentNumber,
        String currency,
        BigDecimal cogsAmount,
        LocalDateTime postingDate
) {}
