package com.digipals.wms.purchaserequisition.controller;

import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import com.digipals.wms.purchaserequisition.service.QuotationToPurchaseRequisitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-requisitions")
@RequiredArgsConstructor
public class QuotationToPurchaseRequisitionController {

    private final QuotationToPurchaseRequisitionService service;

    @PostMapping("/{requisitionId}/import-quotation/{quotationId}")
    public PurchaseRequisitionResponse importQuotationLines(
            @PathVariable UUID requisitionId,
            @PathVariable UUID quotationId) {
        return service.importLines(requisitionId, quotationId);
    }
}
