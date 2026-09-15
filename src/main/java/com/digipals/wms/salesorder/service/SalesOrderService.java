package com.digipals.wms.salesorder.service;

import com.digipals.wms.salesorder.dto.CreateSalesOrderRequest;
import com.digipals.wms.salesorder.entity.SalesOrder;

import java.util.List;
import java.util.UUID;

public interface SalesOrderService {

    SalesOrder create(CreateSalesOrderRequest request);

    SalesOrder findById(UUID id);

    SalesOrder findByOrderNumber(String orderNumber);

    List<SalesOrder> findAll();

    List<SalesOrder> findByCustomerCode(String customerCode);
}
