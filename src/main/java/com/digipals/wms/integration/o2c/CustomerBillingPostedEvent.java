package com.digipals.wms.integration.o2c;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerBillingPostedEvent(
        UUID sourceDocumentId,
        String sourceDocumentNumber,
        String currency,
        BigDecimal revenueAmount,
        LocalDateTime postingDate
) {}
