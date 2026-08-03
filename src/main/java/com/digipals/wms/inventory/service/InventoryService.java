package com.digipals.wms.inventory.service;

import com.digipals.wms.inventory.entity.Inventory;
import com.digipals.wms.products.Product;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InventoryService {

        Inventory create(Inventory inventory);

        List<Inventory> findAll();

        Inventory findById(UUID id);

        List<Inventory> findByWarehouse(UUID warehouseId);

        List<Inventory> findByProduct(UUID productId);

        /**
         * Manual stock adjustment.
         */
        Inventory adjustStock(
                        UUID inventoryId,
                        BigDecimal quantity);

    /**
     * Receives stock into inventory.
     * Creates the inventory record automatically if it does not exist.
     */
    Inventory receiveStock(
            /*Warehouse warehouse,
            Product product,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks,
            User performedBy */
        
                Warehouse,
                Bin,
                Product,
                Quantity,
                Reference,
                ReferenceType,
                Remarks,
                User
                        
                        
        );

        /**
         * Issues stock from inventory.
         * Throws an exception if there is insufficient stock.
         */
        Inventory issueStock(
                        Warehouse warehouse,
                        Product product,
                        BigDecimal quantity,
                        String referenceNumber,
                        String referenceType,
                        String remarks,
                        User performedBy);
}