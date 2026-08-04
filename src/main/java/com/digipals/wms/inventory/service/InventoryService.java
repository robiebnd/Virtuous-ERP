package com.digipals.wms.inventory.service;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.products.Product;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InventoryService {

    InventoryBin create(InventoryBin inventoryBin);

    List<InventoryBin> findAll();

    InventoryBin findById(UUID id);

    List<InventoryBin> findByWarehouse(UUID warehouseId);

    List<InventoryBin> findByProduct(UUID productId);

    /**
     * Manual stock adjustment.
     */
    InventoryBin adjustStock(
            UUID inventoryBinId,
            BigDecimal quantity);

    /**
     * Receives stock into a specific bin.
     * Creates the inventory record automatically if it does not exist.
     */
    InventoryBin receiveStock(
            Warehouse warehouse,
            Bin bin,
            Product product,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks,
            User performedBy);

    /**
     * Issues stock from a specific bin.
     */
    InventoryBin issueStock(
            Warehouse warehouse,
            Bin bin,
            Product product,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks,
            User performedBy);

    /**
     * Moves stock from one bin to another.
     */
    void moveStock(
            Warehouse warehouse,
            Bin fromBin,
            Bin toBin,
            Product product,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks,
            User performedBy);

    /**
     * Reserve stock for picking or allocation.
     */
    InventoryBin reserveStock(
            UUID inventoryBinId,
            BigDecimal quantity);

    /**
     * Release previously reserved stock.
     */
    InventoryBin releaseReservation(
            UUID inventoryBinId,
            BigDecimal quantity);

    /**
     * Returns available quantity
     * (On Hand - Reserved).
     */
    BigDecimal availableStock(UUID inventoryBinId);

    /**
     * Checks whether inventory exists.
     */
    boolean inventoryExists(
            UUID warehouseId,
            UUID binId,
            UUID productId);

    /**
     * Returns inventory for a warehouse/bin/product combination.
     */
    InventoryBin getInventory(
            UUID warehouseId,
            UUID binId,
            UUID productId);
}