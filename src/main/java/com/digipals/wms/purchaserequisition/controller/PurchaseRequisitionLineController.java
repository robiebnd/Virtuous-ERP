package com.digipals.wms.purchaserequisition.controller;

import com.digipals.wms.purchaserequisition.dto.CreatePurchaseRequisitionLineRequest;
import com.digipals.wms.purchaserequisition.dto.PurchaseRequisitionLineResponse;
import com.digipals.wms.purchaserequisition.dto.UpdatePurchaseRequisitionLineRequest;
import com.digipals.wms.purchaserequisition.service.PurchaseRequisitionLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-requisition-lines")
@RequiredArgsConstructor
public class PurchaseRequisitionLineController {

    private final PurchaseRequisitionLineService service;

    /**
     * Create Purchase Requisition Line
     */
    @PostMapping
    public PurchaseRequisitionLineResponse create(
            @Valid
            @RequestBody
            CreatePurchaseRequisitionLineRequest request) {

        return service.create(request);
    }

    /**
     * Get All Purchase Requisition Lines
     */
    @GetMapping
    public List<PurchaseRequisitionLineResponse> findAll() {

        return service.findAll();
    }

    /**
     * Get Purchase Requisition Line By Id
     */
    @GetMapping("/{id}")
    public PurchaseRequisitionLineResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    /**
     * Get Lines For Purchase Requisition
     */
    @GetMapping("/purchase-requisition/{purchaseRequisitionId}")
    public List<PurchaseRequisitionLineResponse> findByPurchaseRequisition(
            @PathVariable UUID purchaseRequisitionId) {

        return service.findByPurchaseRequisition(
                purchaseRequisitionId);
    }

    /**
     * Update Purchase Requisition Line
     */
    @PutMapping("/{id}")
    public PurchaseRequisitionLineResponse update(
            @PathVariable UUID id,
            @Valid
            @RequestBody
            UpdatePurchaseRequisitionLineRequest request) {

        return service.update(id, request);
    }



    /**
     * Delete Purchase Requisition Line
     */
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}