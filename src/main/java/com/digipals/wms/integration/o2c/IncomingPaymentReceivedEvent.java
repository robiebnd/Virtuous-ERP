package com.digipals.wms.integration.o2c;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record IncomingPaymentReceivedEvent(
        UUID sourceDocumentId,
        String sourceDocumentNumber,
        String currency,
        BigDecimal paymentAmount,
        BigDecimal appliedAmount,
        LocalDateTime postingDate
) {}
