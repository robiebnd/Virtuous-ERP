package com.digipals.wms.inventorybin.repository;

import com.digipals.wms.inventorybin.entity.InventoryBin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryBinRepository extends JpaRepository<InventoryBin, UUID> {

    List<InventoryBin> findByWarehouseId(UUID warehouseId);

    List<InventoryBin> findByBinId(UUID binId);

    List<InventoryBin> findByProductId(UUID productId);

    Optional<InventoryBin> findByWarehouseIdAndBinIdAndProductId(
            UUID warehouseId,
            UUID binId,
            UUID productId);

    boolean existsByWarehouseIdAndBinIdAndProductId(
            UUID warehouseId,
            UUID binId,
            UUID productId);
}