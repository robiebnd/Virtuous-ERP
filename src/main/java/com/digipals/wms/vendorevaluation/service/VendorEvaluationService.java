package com.digipals.wms.vendorevaluation.service;

import com.digipals.wms.vendorevaluation.dto.CreateVendorEvaluationRequest;
import com.digipals.wms.vendorevaluation.entity.VendorEvaluation;
import java.util.List;
import java.util.UUID;

public interface VendorEvaluationService {
    VendorEvaluation create(CreateVendorEvaluationRequest request);
    List<VendorEvaluation> findBySupplier(UUID supplierId);
}
