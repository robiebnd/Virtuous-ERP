package com.digipals.wms.procurement.controller;

import com.digipals.wms.procurement.dto.GeneratePurchaseOrderRequest;
import com.digipals.wms.procurement.service.ProcurementService;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/procurement")
@RequiredArgsConstructor
public class ProcurementController {

    private final ProcurementService service;

    @PostMapping("/generate-purchase-order")
    public PurchaseOrderResponse generatePurchaseOrder(
            @Valid
            @RequestBody
            GeneratePurchaseOrderRequest request) {

        return service.generatePurchaseOrder(request);
    }

    /**
     * Produces an explainable procurement recommendation for an approved PR.
     * This endpoint does not create or approve a PO.
     */
    @GetMapping("/purchase-requisitions/{purchaseRequisitionId}/recommendation")
    public Map<String, Object> recommendPurchaseOrder(
            @PathVariable UUID purchaseRequisitionId) {

        return service.recommendPurchaseOrder(purchaseRequisitionId);
    }
}
