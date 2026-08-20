package com.digipals.wms.purchasinginforecord.service;

import com.digipals.wms.purchasinginforecord.dto.PurchasingInfoRecordRequest;
import com.digipals.wms.purchasinginforecord.dto.PurchasingInfoRecordResponse;

import java.util.List;
import java.util.UUID;

public interface PurchasingInfoRecordService {

    PurchasingInfoRecordResponse create(PurchasingInfoRecordRequest request);

    PurchasingInfoRecordResponse update(UUID id, PurchasingInfoRecordRequest request);

    PurchasingInfoRecordResponse findById(UUID id);

    PurchasingInfoRecordResponse findBySupplierProductAndWarehouse(UUID supplierProductIdentifierId, UUID warehouseId);

    List<PurchasingInfoRecordResponse> findBySupplierProduct(UUID supplierProductIdentifierId);

    List<PurchasingInfoRecordResponse> findByWarehouse(UUID warehouseId);

    List<PurchasingInfoRecordResponse> findAll();
}
