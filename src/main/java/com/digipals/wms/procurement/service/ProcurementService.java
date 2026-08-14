package com.digipals.wms.procurement.service;

import com.digipals.wms.procurement.dto.GeneratePurchaseOrderRequest;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;

public interface ProcurementService {

    PurchaseOrderResponse generatePurchaseOrder(
            GeneratePurchaseOrderRequest request);
}
