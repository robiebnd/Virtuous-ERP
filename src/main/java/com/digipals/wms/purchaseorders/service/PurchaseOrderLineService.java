package com.digipals.wms.purchaseorders.service;

import com.digipals.wms.purchaseorders.dto.CreatePurchaseOrderLineRequest;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderLineResponse;
import com.digipals.wms.purchaseorders.dto.UpdatePurchaseOrderLineRequest;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderLineService {

    PurchaseOrderLineResponse create(
            CreatePurchaseOrderLineRequest request);

    PurchaseOrderLineResponse update(
            UUID id,
            UpdatePurchaseOrderLineRequest request);

    PurchaseOrderLineResponse findById(
            UUID id);

    List<PurchaseOrderLineResponse> findAll();

    List<PurchaseOrderLineResponse> findByPurchaseOrder(
            UUID purchaseOrderId);

    void delete(
            UUID id);
}