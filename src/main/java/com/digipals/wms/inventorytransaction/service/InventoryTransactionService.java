package com.digipals.wms.inventorytransaction.service;

import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InventoryTransactionService {

    InventoryTransaction receiveStock(UUID warehouseId, UUID binId, UUID productId,
                                      BigDecimal quantity, String referenceNumber,
                                      String referenceType, String remarks);

    InventoryTransaction issueStock(UUID warehouseId, UUID binId, UUID productId,
                                    BigDecimal quantity, String referenceNumber,
                                    String referenceType, String remarks);

    InventoryTransaction adjustStock(UUID warehouseId, UUID binId, UUID productId,
                                     BigDecimal quantity, TransactionType transactionType,
                                     String referenceNumber, String remarks);

    List<InventoryTransaction> findAll();
    List<InventoryTransaction> findByInventoryBin(UUID inventoryBinId);
    List<InventoryTransaction> findByBin(UUID binId);
    List<InventoryTransaction> findByReferenceNumber(String referenceNumber);
    List<InventoryTransaction> findByReferenceType(String referenceType);
    List<InventoryTransaction> findByWarehouseCode(String warehouseCode);
    List<InventoryTransaction> findBySku(String sku);
    InventoryTransaction findById(UUID id);
}
