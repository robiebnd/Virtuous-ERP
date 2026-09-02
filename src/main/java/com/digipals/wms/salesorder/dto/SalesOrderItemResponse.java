package com.digipals.wms.salesorder.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SalesOrderItemResponse(
        UUID id,
        Integer itemNumber,
        String materialCode,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal netValue
) {
}
