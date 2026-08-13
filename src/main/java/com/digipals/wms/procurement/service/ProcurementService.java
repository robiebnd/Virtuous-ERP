package com.digipals.wms.procurement.service;

import com.digipals.wms.procurement.dto.GeneratePurchaseOrderRequest;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;

import java.util.Map;
import java.util.UUID;

public interface ProcurementService {

    PurchaseOrderResponse generatePurchaseOrder(
            GeneratePurchaseOrderRequest request);

    Map<String, Object> recommendPurchaseOrder(UUID purchaseRequisitionId);
}
