package com.digipals.wms.inventorymovement.service;

import com.digipals.wms.inventorymovement.dto.InventoryMovementResponse;
import com.digipals.wms.inventorymovement.entity.InventoryMovement;

import java.util.List;
import java.util.UUID;

public interface InventoryMovementService {
    InventoryMovementResponse create(InventoryMovement movement);
    List<InventoryMovementResponse> findAll();
    List<InventoryMovementResponse> findBySku(String sku);
    List<InventoryMovementResponse> findByWarehouse(UUID warehouseId);
    List<InventoryMovementResponse> findByReference(String referenceType, UUID referenceId);
}
