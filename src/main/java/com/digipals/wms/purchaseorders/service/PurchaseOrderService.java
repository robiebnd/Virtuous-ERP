package com.digipals.wms.purchaseorders.service;

import com.digipals.wms.purchaseorders.dto.UpdatePurchaseOrderRequest;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderService {

    PurchaseOrder createFromRequisition(
            UUID purchaseRequisitionId);

    PurchaseOrder update(
            UUID id,
            UpdatePurchaseOrderRequest request);

    List<PurchaseOrder> findAll();

    PurchaseOrder findById(UUID id);

    PurchaseOrder approve(UUID id);

    PurchaseOrder receive(UUID id);
}
