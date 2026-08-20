package com.digipals.wms.purchasinginforecord.controller;

import com.digipals.wms.purchasinginforecord.dto.ApplySourceOfSupplyRequest;
import com.digipals.wms.purchasinginforecord.service.SourceOfSupplyService;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/source-of-supply")
@RequiredArgsConstructor
public class SourceOfSupplyController {

    private final SourceOfSupplyService service;

    @GetMapping("/simulate")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public Map<String, Object> simulate(
            @RequestParam UUID productId,
            @RequestParam UUID warehouseId,
            @RequestParam(required = false) LocalDate deliveryDate) {
        return service.simulate(productId, warehouseId, deliveryDate);
    }

    @GetMapping("/requisition/{requisitionId}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public Map<String, Object> determineForRequisition(
            @PathVariable UUID requisitionId,
            @RequestParam(required = false) LocalDate deliveryDate) {
        return service.determineForRequisition(requisitionId, deliveryDate);
    }

    @PutMapping("/requisition/{requisitionId}/line/{lineId}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_UPDATE')")
    public PurchaseRequisitionResponse apply(
            @PathVariable UUID requisitionId,
            @PathVariable UUID lineId,
            @Valid @RequestBody ApplySourceOfSupplyRequest request) {
        return service.apply(requisitionId, lineId, request);
    }
}
