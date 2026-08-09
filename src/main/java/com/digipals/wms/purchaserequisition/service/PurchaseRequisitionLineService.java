package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.purchaserequisition.dto.CreatePurchaseRequisitionLineRequest;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionLineResponse;
import com.digipals.wms.purchaserequisition.dto.UpdatePurchaseRequisitionLineRequest;

import java.util.List;
import java.util.UUID;

public interface PurchaseRequisitionLineService {

    PurchaseRequisitionLineResponse create(
            UUID purchaseRequisitionId,
            CreatePurchaseRequisitionLineRequest request);

    PurchaseRequisitionLineResponse update(
            UUID id,
            UpdatePurchaseRequisitionLineRequest request);

    PurchaseRequisitionLineResponse findById(
            UUID id);

    List<PurchaseRequisitionLineResponse> findAll();

    List<PurchaseRequisitionLineResponse> findByPurchaseRequisition(
            UUID purchaseRequisitionId);

    void delete(
            UUID id);
}
