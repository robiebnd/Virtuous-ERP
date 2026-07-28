package com.digipals.wms.inventory.repository;

import com.digipals.wms.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository
        extends JpaRepository<Inventory, UUID> {

    List<Inventory> findByWarehouseId(
            UUID warehouseId);

    List<Inventory> findByProductId(
            UUID productId);

    Optional<Inventory>
    findByWarehouseIdAndProductId(
            UUID warehouseId,
            UUID productId);
}
