package com.digipals.wms.purchaseorders.controller;

import com.digipals.wms.purchaseorders.dto.CreatePurchaseOrderLineRequest;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderLineResponse;
import com.digipals.wms.purchaseorders.dto.UpdatePurchaseOrderLineRequest;
import com.digipals.wms.purchaseorders.service.PurchaseOrderLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-order-lines")
@RequiredArgsConstructor
public class PurchaseOrderLineController {

    private final PurchaseOrderLineService service;

    @PostMapping
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_CREATE')")
    public PurchaseOrderLineResponse create(

            @Valid
            @RequestBody
            CreatePurchaseOrderLineRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_UPDATE')")
    public PurchaseOrderLineResponse update(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdatePurchaseOrderLineRequest request) {

        return service.update(id, request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public List<PurchaseOrderLineResponse> getAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public PurchaseOrderLineResponse getById(

            @PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping("/purchase-order/{purchaseOrderId}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public List<PurchaseOrderLineResponse> getByPurchaseOrder(

            @PathVariable UUID purchaseOrderId) {

        return service.findByPurchaseOrder(
                purchaseOrderId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_DELETE')")
    public void delete(

            @PathVariable UUID id) {

        service.delete(id);
    }
}