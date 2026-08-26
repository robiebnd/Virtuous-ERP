package com.digipals.wms.inventorytransaction.repository;

import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, UUID> {

    List<InventoryTransaction> findByInventoryBinIdOrderByTransactionDateDesc(UUID inventoryBinId);

    List<InventoryTransaction> findByFromBinIdOrderByTransactionDateDesc(UUID fromBinId);

    List<InventoryTransaction> findByToBinIdOrderByTransactionDateDesc(UUID toBinId);

    List<InventoryTransaction> findByReferenceNumberOrderByTransactionDateDesc(String referenceNumber);

    List<InventoryTransaction> findByReferenceTypeOrderByTransactionDateDesc(String referenceType);

    List<InventoryTransaction> findByTransactionTypeOrderByTransactionDateDesc(TransactionType transactionType);

    List<InventoryTransaction> findByInventoryBin_Warehouse_CodeOrderByTransactionDateDesc(String warehouseCode);

    List<InventoryTransaction> findByInventoryBin_Product_SkuOrderByTransactionDateDesc(String sku);
}
