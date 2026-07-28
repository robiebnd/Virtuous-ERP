package com.digipals.wms.inventorytransaction.service;

import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InventoryTransactionService {

    InventoryTransaction receiveStock(

            UUID warehouseId,

            UUID productId,

            BigDecimal quantity,

            String referenceNumber,

            String referenceType,

            String remarks);

    InventoryTransaction issueStock(

            UUID warehouseId,

            UUID productId,

            BigDecimal quantity,

            String referenceNumber,

            String referenceType,

            String remarks);

    InventoryTransaction adjustStock(

            UUID warehouseId,

            UUID productId,

            BigDecimal quantity,

            TransactionType transactionType,

            String referenceNumber,

            String remarks);

    List<InventoryTransaction> findAll();

    List<InventoryTransaction> findByInventory(
            UUID inventoryId);

    InventoryTransaction findById(
            UUID id);
}