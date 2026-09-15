package com.digipals.wms.documentflow.service;

import com.digipals.wms.documentflow.dto.DocumentFlowResponse;

import java.util.UUID;

public interface DocumentFlowService {
    DocumentFlowResponse getBySalesOrderId(UUID salesOrderId);
    DocumentFlowResponse getByDeliveryId(UUID deliveryId);
    DocumentFlowResponse getByBillingDocumentId(UUID billingDocumentId);
}
