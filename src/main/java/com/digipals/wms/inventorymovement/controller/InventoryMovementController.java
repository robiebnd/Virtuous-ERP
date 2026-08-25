package com.digipals.wms.inventorymovement.controller;

import com.digipals.wms.inventorymovement.dto.InventoryMovementResponse;
import com.digipals.wms.inventorymovement.entity.InventoryMovement;
import com.digipals.wms.inventorymovement.service.InventoryMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory-movements")
@RequiredArgsConstructor
public class InventoryMovementController {

    private final InventoryMovementService service;

    @GetMapping
    public List<InventoryMovementResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/sku/{sku}")
    public List<InventoryMovementResponse> findBySku(@PathVariable String sku) {
        return service.findBySku(sku);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public List<InventoryMovementResponse> findByWarehouse(@PathVariable UUID warehouseId) {
        return service.findByWarehouse(warehouseId);
    }

    @GetMapping("/reference/{referenceType}/{referenceId}")
    public List<InventoryMovementResponse> findByReference(
            @PathVariable String referenceType,
            @PathVariable UUID referenceId) {
        return service.findByReference(referenceType, referenceId);
    }

    /**
     * Internal/manual creation endpoint. Business workflows should create
     * movements automatically when GRNs are approved or put-aways completed.
     */
    @PostMapping
    public InventoryMovementResponse create(@RequestBody InventoryMovement movement) {
        return service.create(movement);
    }
}
