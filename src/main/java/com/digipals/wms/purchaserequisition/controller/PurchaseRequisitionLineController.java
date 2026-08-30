package com.digipals.wms.purchaserequisition.controller;

import com.digipals.wms.common.exception.ResourceNotFoundException;
import com.digipals.wms.purchaserequisition.dto.CreatePurchaseRequisitionLineRequest;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionLineResponse;
import com.digipals.wms.purchaserequisition.dto.SetPurchaseRequisitionLineSourceRequest;
import com.digipals.wms.purchaserequisition.dto.UpdatePurchaseRequisitionLineRequest;
import com.digipals.wms.purchaserequisition.service.PurchaseRequisitionLineService;
import com.digipals.wms.purchaserequisition.service.PurchaseRequisitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-requisitions")
@RequiredArgsConstructor
public class PurchaseRequisitionLineController {

    private final PurchaseRequisitionLineService service;
    private final PurchaseRequisitionService purchaseRequisitionService;

    @PostMapping("/{requisitionId}/lines")
    public PurchaseRequisitionLineResponse create(
            @PathVariable UUID requisitionId,
            @Valid @RequestBody CreatePurchaseRequisitionLineRequest request) {
        return service.create(requisitionId, request);
    }

    /**
     * Frontend-facing route. Requisition numbers are business identifiers;
     * UUIDs remain internal implementation details.
     */
    @PostMapping("/number/{requisitionNumber}/lines")
    public PurchaseRequisitionLineResponse createByNumber(
            @PathVariable String requisitionNumber,
            @Valid @RequestBody CreatePurchaseRequisitionLineRequest request) {
        String normalizedNumber = requisitionNumber == null ? "" : requisitionNumber.trim();
        if (normalizedNumber.isBlank()) {
            throw new IllegalArgumentException("Purchase Requisition number is required.");
        }

        PurchaseRequisitionResponseMatch match = purchaseRequisitionService.findAll().stream()
                .filter(item -> normalizedNumber.equalsIgnoreCase(item.getRequisitionNumber()))
                .map(item -> new PurchaseRequisitionResponseMatch(item.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase Requisition not found: " + normalizedNumber));

        return service.create(match.id(), request);
    }

    @PostMapping("/lines/{id}/source-of-supply")
    public PurchaseRequisitionLineResponse setSourceOfSupply(
            @PathVariable UUID id,
            @Valid @RequestBody SetPurchaseRequisitionLineSourceRequest request) {
        return service.setSourceOfSupply(id, request);
    }

    @GetMapping("/{requisitionId}/lines")
    public List<PurchaseRequisitionLineResponse> findByPurchaseRequisition(
            @PathVariable UUID requisitionId) {
        return service.findByPurchaseRequisition(requisitionId);
    }

    @GetMapping("/lines/{id}")
    public PurchaseRequisitionLineResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/lines/{id}")
    public PurchaseRequisitionLineResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePurchaseRequisitionLineRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/lines/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    private record PurchaseRequisitionResponseMatch(UUID id) {}
}
