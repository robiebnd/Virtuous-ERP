package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.purchaserequisition.dto.*;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;

import java.util.List;
import java.util.UUID;

public interface PurchaseRequisitionService {

    PurchaseRequisitionResponse create(CreatePurchaseRequisitionRequest request);
    PurchaseRequisitionResponse update(UUID id, UpdatePurchaseRequisitionRequest request);
    PurchaseRequisitionResponse findById(UUID id);
    List<PurchaseRequisitionResponse> findAll();
    List<PurchaseRequisitionResponse> findByStatus(PurchaseRequisitionStatus status);
    List<PurchaseRequisitionResponse> findByWarehouse(UUID warehouseId);
    PurchaseRequisitionResponse submit(UUID id);
    PurchaseRequisitionResponse submitByNumber(String requisitionNumber);
    PurchaseRequisitionResponse approve(UUID id);
    PurchaseRequisitionResponse approveByNumber(String requisitionNumber);
    PurchaseRequisitionResponse reject(UUID id, String remarks);
    PurchaseRequisitionResponse rejectByNumber(String requisitionNumber, String remarks);
    PurchaseRequisitionResponse cancel(UUID id);
    PurchaseRequisitionResponse cancelByNumber(String requisitionNumber);
    void delete(UUID id);
    void clearLines(UUID id);
    PurchaseRequisitionResponse importQuotation(UUID requisitionId, UUID quotationId);
    PurchaseRequisitionResponse importQuotationByNumber(UUID requisitionId, String quotationNumber);
}
