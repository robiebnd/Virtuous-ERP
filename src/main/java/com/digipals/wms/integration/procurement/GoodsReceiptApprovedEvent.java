package com.digipals.wms.integration.procurement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Integration contract published by Procurement when an approved goods receipt
 * has a positive receipt value that requires financial recognition.
 */
public record GoodsReceiptApprovedEvent(
        UUID sourceDocumentId,
        String sourceDocumentNumber,
        String currency,
        BigDecimal amount,
        LocalDateTime postingDate
) {}
