package com.digipals.wms.billing.service;

import com.digipals.wms.billing.dto.CreateBillingRequest;
import com.digipals.wms.billing.entity.BillingDocument;

import java.util.List;
import java.util.UUID;

public interface BillingDocumentService {
    BillingDocument create(CreateBillingRequest request);
    BillingDocument post(UUID id);
    BillingDocument findById(UUID id);
    BillingDocument findByBillingNumber(String billingNumber);
    List<BillingDocument> findAll();
    List<BillingDocument> findByCustomerCode(String customerCode);
}
