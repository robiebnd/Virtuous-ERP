package com.digipals.wms.inventorytransaction.controller;

import com.digipals.wms.common.mapper.InventoryTransactionMapper;
import com.digipals.wms.inventorytransaction.dto.InventoryTransactionResponse;
import com.digipals.wms.inventorytransaction.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService service;

    @GetMapping
    public List<InventoryTransactionResponse> findAll() {
        return service.findAll().stream().map(InventoryTransactionMapper::toResponse).toList();
    }

    /** Frontend lookup by business document number. */
    @GetMapping("/reference/{referenceNumber}")
    public List<InventoryTransactionResponse> findByReferenceNumber(
            @PathVariable String referenceNumber) {
        return service.findByReferenceNumber(referenceNumber)
                .stream().map(InventoryTransactionMapper::toResponse).toList();
    }

    /** Frontend lookup by document type and business document number. */
    @GetMapping("/reference/{referenceType}/{referenceNumber}")
    public List<InventoryTransactionResponse> findByReference(
            @PathVariable String referenceType,
            @PathVariable String referenceNumber) {
        return service.findByReferenceNumber(referenceNumber).stream()
                .filter(t -> referenceType.equalsIgnoreCase(t.getReferenceType()))
                .map(InventoryTransactionMapper::toResponse)
                .toList();
    }

    /** Frontend lookup by warehouse business code. */
    @GetMapping("/warehouse/{warehouseCode}")
    public List<InventoryTransactionResponse> findByWarehouseCode(
            @PathVariable String warehouseCode) {
        return service.findByWarehouseCode(warehouseCode)
                .stream().map(InventoryTransactionMapper::toResponse).toList();
    }

    /** Frontend lookup by product SKU. */
    @GetMapping("/sku/{sku}")
    public List<InventoryTransactionResponse> findBySku(
            @PathVariable String sku) {
        return service.findBySku(sku)
                .stream().map(InventoryTransactionMapper::toResponse).toList();
    }

    /** Internal lookup by UUID. Keep this endpoint for backend/internal use only. */
    @GetMapping("/internal/{id}")
    public InventoryTransactionResponse findById(@PathVariable java.util.UUID id) {
        return InventoryTransactionMapper.toResponse(service.findById(id));
    }
}
