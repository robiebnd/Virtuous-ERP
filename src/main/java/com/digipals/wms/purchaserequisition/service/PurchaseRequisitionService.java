package com.digipals.wms.purchaserequisition.service;

import com.digipals.wms.purchaserequisition.dto.*;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;

import java.util.List;
import java.util.UUID;

public interface PurchaseRequisitionService {

    PurchaseRequisitionResponse create(
            CreatePurchaseRequisitionRequest request);

    PurchaseRequisitionResponse update(
            UUID id,
            UpdatePurchaseRequisitionRequest request);

    PurchaseRequisitionResponse findById(
            UUID id);

    PurchaseRequisitionResponse findByRequisitionNumber(
            String requisitionNumber);

    List<PurchaseRequisitionResponse> findAll();

    List<PurchaseRequisitionResponse> findByStatus(
            PurchaseRequisitionStatus status);

    List<PurchaseRequisitionResponse> findByWarehouse(
            UUID warehouseId);

    PurchaseRequisitionResponse submit(
            UUID id);

    PurchaseRequisitionResponse approve(
            UUID id);

    PurchaseRequisitionResponse reject(
            UUID id,
            String remarks);

    PurchaseRequisitionResponse cancel(
            UUID id);

    void delete(
            UUID id);
}