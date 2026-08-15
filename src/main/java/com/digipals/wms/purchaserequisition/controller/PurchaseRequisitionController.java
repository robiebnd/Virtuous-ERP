package com.digipals.wms.purchaserequisition.controller;

import com.digipals.wms.purchaserequisition.dto.CreatePurchaseRequisitionRequest;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionResponse;
import com.digipals.wms.purchaserequisition.dto.UpdatePurchaseRequisitionRequest;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import com.digipals.wms.purchaserequisition.service.PurchaseRequisitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-requisitions")
@RequiredArgsConstructor
public class PurchaseRequisitionController {
    private final PurchaseRequisitionService service;

    @PostMapping
    public PurchaseRequisitionResponse create(@Valid @RequestBody CreatePurchaseRequisitionRequest request) { return service.create(request); }
    @GetMapping
    public List<PurchaseRequisitionResponse> findAll() { return service.findAll(); }
    @GetMapping("/{id}")
    public PurchaseRequisitionResponse findById(@PathVariable UUID id) { return service.findById(id); }
    @GetMapping("/status/{status}")
    public List<PurchaseRequisitionResponse> findByStatus(@PathVariable PurchaseRequisitionStatus status) { return service.findByStatus(status); }
    @GetMapping("/warehouse/{warehouseId}")
    public List<PurchaseRequisitionResponse> findByWarehouse(@PathVariable UUID warehouseId) { return service.findByWarehouse(warehouseId); }
    @PutMapping("/{id}")
    public PurchaseRequisitionResponse update(@PathVariable UUID id, @Valid @RequestBody UpdatePurchaseRequisitionRequest request) { return service.update(id, request); }
    @PostMapping("/{id}/submit")
    public PurchaseRequisitionResponse submit(@PathVariable UUID id) { return service.submit(id); }
    @PutMapping("/{id}/approve")
    public PurchaseRequisitionResponse approve(@PathVariable UUID id) { return service.approve(id); }
    @PutMapping("/{id}/reject")
    public PurchaseRequisitionResponse reject(@PathVariable UUID id, @RequestParam String remarks) { return service.reject(id, remarks); }
    @PostMapping("/{id}/cancel")
    public PurchaseRequisitionResponse cancel(@PathVariable UUID id) { return service.cancel(id); }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.delete(id); }

    @PostMapping("/{requisitionId}/import-quotation/{quotationId}")
    public PurchaseRequisitionResponse importQuotation(@PathVariable UUID requisitionId, @PathVariable UUID quotationId) {
        return service.importQuotation(requisitionId, quotationId);
    }
}
