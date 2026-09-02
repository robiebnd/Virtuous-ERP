package com.digipals.wms.salesorder.sap;

import java.util.List;

public record SapSalesOrderRequest(
        String customerCode,
        String salesOrganization,
        String distributionChannel,
        String division,
        List<SapSalesOrderItem> items
) {
}
