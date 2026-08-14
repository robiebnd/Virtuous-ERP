package com.digipals.wms.supplierquotation.controller;

import com.digipals.wms.supplierquotation.dto.SupplierQuotationResponse;
import com.digipals.wms.supplierquotation.service.SupplierQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supplier-quotations")
@RequiredArgsConstructor
public class SupplierQuotationController {

    private final SupplierQuotationService service;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_CREATE')")
    public SupplierQuotationResponse upload(
            @RequestParam UUID purchaseRequisitionId,
            @RequestParam UUID supplierId,
            @RequestParam String quotationNumber,
            @RequestPart MultipartFile file) {
        return service.upload(purchaseRequisitionId, supplierId, quotationNumber, file);
    }

    @GetMapping("/requisition/{purchaseRequisitionId}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public List<SupplierQuotationResponse> findByPurchaseRequisition(
            @PathVariable UUID purchaseRequisitionId) {
        return service.findByPurchaseRequisition(purchaseRequisitionId);
    }
}
