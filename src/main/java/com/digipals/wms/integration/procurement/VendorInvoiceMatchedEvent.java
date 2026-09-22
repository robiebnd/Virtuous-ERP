package com.digipals.wms.integration.procurement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Integration contract published by Procurement when a vendor invoice passes
 * three-way matching and is ready for financial posting.
 */
public record VendorInvoiceMatchedEvent(
        UUID sourceDocumentId,
        String sourceDocumentNumber,
        String currency,
        BigDecimal amount,
        LocalDateTime postingDate
) {}
