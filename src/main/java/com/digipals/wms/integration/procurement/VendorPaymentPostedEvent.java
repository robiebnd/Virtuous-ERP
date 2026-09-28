package com.digipals.wms.integration.procurement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VendorPaymentPostedEvent(
        UUID sourceDocumentId,
        String sourceDocumentNumber,
        String currency,
        BigDecimal amount,
        LocalDateTime postingDate
) {}
