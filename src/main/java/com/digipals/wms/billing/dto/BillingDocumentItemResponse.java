package com.digipals.wms.billing.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BillingDocumentItemResponse(
        UUID id,
        Integer itemNumber,
        String materialCode,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal netValue
) {
}
