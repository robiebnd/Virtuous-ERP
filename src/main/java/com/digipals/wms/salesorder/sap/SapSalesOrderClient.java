package com.digipals.wms.salesorder.sap;

public interface SapSalesOrderClient {

    SapSalesOrderResponse createSalesOrder(SapSalesOrderRequest request);
}
