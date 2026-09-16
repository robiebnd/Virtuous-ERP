package com.digipals.wms.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OpenItemResponse(
        UUID id,
        String itemType,
        String referenceNumber,
        String partyCode,
        String partyName,
        LocalDateTime documentDate,
        LocalDateTime dueDate,
        String currency,
        BigDecimal originalAmount,
        BigDecimal clearedAmount,
        BigDecimal openAmount,
        long ageingDays,
        String ageingBucket,
        String status
) {}
