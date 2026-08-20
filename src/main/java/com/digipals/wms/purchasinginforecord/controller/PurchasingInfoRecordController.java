package com.digipals.wms.purchasinginforecord.controller;

import com.digipals.wms.purchasinginforecord.dto.PurchasingInfoRecordRequest;
import com.digipals.wms.purchasinginforecord.dto.PurchasingInfoRecordResponse;
import com.digipals.wms.purchasinginforecord.service.PurchasingInfoRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchasing-info-records")
@RequiredArgsConstructor
public class PurchasingInfoRecordController {

    private final PurchasingInfoRecordService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_CREATE')")
    public PurchasingInfoRecordResponse create(@Valid @RequestBody PurchasingInfoRecordRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_CREATE')")
    public PurchasingInfoRecordResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody PurchasingInfoRecordRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public List<PurchasingInfoRecordResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public PurchasingInfoRecordResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/supplier-product/{supplierProductIdentifierId}/warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public PurchasingInfoRecordResponse findBySupplierProductAndWarehouse(
            @PathVariable UUID supplierProductIdentifierId,
            @PathVariable UUID warehouseId) {
        return service.findBySupplierProductAndWarehouse(supplierProductIdentifierId, warehouseId);
    }

    @GetMapping("/supplier-product/{supplierProductIdentifierId}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public List<PurchasingInfoRecordResponse> findBySupplierProduct(
            @PathVariable UUID supplierProductIdentifierId) {
        return service.findBySupplierProduct(supplierProductIdentifierId);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('PURCHASE_ORDER_VIEW')")
    public List<PurchasingInfoRecordResponse> findByWarehouse(@PathVariable UUID warehouseId) {
        return service.findByWarehouse(warehouseId);
    }
}
