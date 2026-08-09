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
@RequestMapping("/api/purchase-requisitions")
@RequiredArgsConstructor
public class PurchaseRequisitionLineController {

    private final PurchaseRequisitionLineService service;


    /**
     * Create Purchase Requisition Line.
     *
     * POST
     * /api/purchase-requisitions/{requisitionId}/lines
     */
    @PostMapping("/{requisitionId}/lines")
    public PurchaseRequisitionLineResponse create(
            @PathVariable UUID requisitionId,
            @Valid
            @RequestBody
            CreatePurchaseRequisitionLineRequest request) {

        return service.create(
                requisitionId,
                request);
    }


    /**
     * Get all lines for a Purchase Requisition.
     *
     * GET
     * /api/purchase-requisitions/{requisitionId}/lines
     */
    @GetMapping("/{requisitionId}/lines")
    public List<PurchaseRequisitionLineResponse>
    findByPurchaseRequisition(
            @PathVariable UUID requisitionId) {

        return service.findByPurchaseRequisition(
                requisitionId);
    }


    /**
     * Get Purchase Requisition Line by ID.
     *
     * GET
     * /api/purchase-requisitions/lines/{id}
     */
    @GetMapping("/lines/{id}")
    public PurchaseRequisitionLineResponse findById(
            @PathVariable UUID id) {

        return service.findById(
                id);
    }


    /**
     * Update Purchase Requisition Line.
     *
     * PUT
     * /api/purchase-requisitions/lines/{id}
     */
    @PutMapping("/lines/{id}")
    public PurchaseRequisitionLineResponse update(
            @PathVariable UUID id,
            @Valid
            @RequestBody
            UpdatePurchaseRequisitionLineRequest request) {

        return service.update(
                id,
                request);
    }


    /**
     * Delete Purchase Requisition Line.
     *
     * DELETE
     * /api/purchase-requisitions/lines/{id}
     */
    @DeleteMapping("/lines/{id}")
    public void delete(
            @PathVariable UUID id) {

        service.delete(
                id);
    }
}
