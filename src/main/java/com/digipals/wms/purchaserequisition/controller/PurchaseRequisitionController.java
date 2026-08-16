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

    /**
     * Create Purchase Requisition
     */
    @PostMapping
    public PurchaseRequisitionResponse create(
            @Valid
            @RequestBody
            CreatePurchaseRequisitionRequest request) {

        return service.create(request);
    }

    /**
     * Get All Purchase Requisitions
     */
    @GetMapping
    public List<PurchaseRequisitionResponse> findAll() {

        return service.findAll();
    }

    /**
     * Get Purchase Requisition By Requisition Number
     */
    @GetMapping("/number/{requisitionNumber}")
    public PurchaseRequisitionResponse findByRequisitionNumber(
            @PathVariable String requisitionNumber) {

        return service.findByRequisitionNumber(requisitionNumber);
    }

    /**
     * Get Purchase Requisition By Id
     */
    @GetMapping("/{id}")
    public PurchaseRequisitionResponse findById(
            @PathVariable UUID id) {

        return service.findById(id);
    }

    /**
     * Find Purchase Requisitions By Status
     */
    @GetMapping("/status/{status}")
    public List<PurchaseRequisitionResponse> findByStatus(
            @PathVariable PurchaseRequisitionStatus status) {

        return service.findByStatus(status);
    }

    /**
     * Find Purchase Requisitions By Warehouse
     */
    @GetMapping("/warehouse/{warehouseId}")
    public List<PurchaseRequisitionResponse> findByWarehouse(
            @PathVariable UUID warehouseId) {

        return service.findByWarehouse(warehouseId);
    }

    /**
     * Update Purchase Requisition
     */
    @PutMapping("/{id}")
    public PurchaseRequisitionResponse update(
            @PathVariable UUID id,
            @Valid
            @RequestBody
            UpdatePurchaseRequisitionRequest request) {

        return service.update(id, request);
    }

    /**
     * Submit Purchase Requisition
     */
    @PostMapping("/{id}/submit")
    public PurchaseRequisitionResponse submit(
            @PathVariable UUID id) {

        return service.submit(id);
    }

    /**
     * Approve Purchase Requisition
     */
    @PutMapping("/{id}/approve")
    public PurchaseRequisitionResponse approve(
            @PathVariable UUID id) {

        return service.approve(id);
    }

    /**
     * Reject Purchase Requisition
     */
    @PutMapping("/{id}/reject")
    public PurchaseRequisitionResponse reject(
            @PathVariable UUID id,
            @RequestParam String remarks) {

        return service.reject(id, remarks);
    }

    /**
     * Cancel Purchase Requisition
     */
    @PostMapping("/{id}/cancel")
    public PurchaseRequisitionResponse cancel(
            @PathVariable UUID id) {

        return service.cancel(id);
    }

    /**
     * Delete Purchase Requisition
     */
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(id);
    }
}