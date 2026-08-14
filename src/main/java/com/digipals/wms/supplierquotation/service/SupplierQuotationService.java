package com.digipals.wms.supplierquotation.service;

import com.digipals.wms.supplierquotation.dto.SupplierQuotationResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface SupplierQuotationService {

    SupplierQuotationResponse upload(UUID purchaseRequisitionId,
                                     UUID supplierId,
                                     String quotationNumber,
                                     MultipartFile file);

    List<SupplierQuotationResponse> findByPurchaseRequisition(UUID purchaseRequisitionId);
}
