package com.digipals.wms.purchaseorders.controller;

import com.digipals.wms.common.mapper.PurchaseOrderMapper;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import com.digipals.wms.purchaseorders.dto.UpdatePurchaseOrderRequest;
import com.digipals.wms.purchaseorders.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    @PostMapping("/from-requisition/{requisitionId}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_CREATE')")
    public PurchaseOrderResponse createFromRequisition(
            @PathVariable UUID requisitionId) {

        return PurchaseOrderMapper.toResponse(
                service.createFromRequisition(requisitionId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public List<PurchaseOrderResponse> getAll() {
        return service.findAll()
                .stream()
                .map(PurchaseOrderMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public PurchaseOrderResponse getById(@PathVariable UUID id) {
        return PurchaseOrderMapper.toResponse(service.findById(id));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_APPROVE')")
    public PurchaseOrderResponse approve(@PathVariable UUID id) {
        return PurchaseOrderMapper.toResponse(service.approve(id));
    }

    @PutMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_RECEIVE')")
    public PurchaseOrderResponse receive(@PathVariable UUID id) {
        return PurchaseOrderMapper.toResponse(service.receive(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_UPDATE')")
    public PurchaseOrderResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePurchaseOrderRequest request) {

        return PurchaseOrderMapper.toResponse(service.update(id, request));
    }
}
