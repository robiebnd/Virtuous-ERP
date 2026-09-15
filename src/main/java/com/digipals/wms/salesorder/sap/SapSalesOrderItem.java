package com.digipals.wms.salesorder.sap;

import java.math.BigDecimal;

public record SapSalesOrderItem(
        String materialCode,
        BigDecimal quantity,
        BigDecimal unitPrice
) {
}
