package com.digipals.wms.inventorymovement.repository;

import com.digipals.wms.inventorymovement.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    List<InventoryMovement> findBySkuOrderByMovementDateDesc(String sku);
    List<InventoryMovement> findByWarehouseIdOrderByMovementDateDesc(UUID warehouseId);
    List<InventoryMovement> findByReferenceTypeAndReferenceId(String referenceType, UUID referenceId);
}
