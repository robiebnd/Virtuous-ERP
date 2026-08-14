package com.digipals.wms.supplierquotation.service;

import java.util.UUID;
import java.util.Map;

public interface QuotationAiService {

    Map<String, Object> recommend(UUID purchaseRequisitionId);
}
