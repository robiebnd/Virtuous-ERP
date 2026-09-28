package com.digipals.wms.integration.o2c;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CashApplicationPostedEvent(
        UUID sourceDocumentId,
        String sourceDocumentNumber,
        String currency,
        BigDecimal amount,
        LocalDateTime postingDate
) {}
