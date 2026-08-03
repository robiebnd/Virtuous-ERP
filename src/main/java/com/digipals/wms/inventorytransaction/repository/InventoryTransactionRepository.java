package com.digipals.wms.inventorytransaction.repository;

import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import com.digipals.wms.inventorytransaction.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, UUID> {

    /**
     * Transaction history for a specific InventoryBin.
     */
    List<InventoryTransaction> findByInventoryBinId(
            UUID inventoryBinId);

    /**
     * All transactions for a source bin.
     */
    List<InventoryTransaction> findByFromBinId(
            UUID fromBinId);

    /**
     * All transactions for a destination bin.
     */
    List<InventoryTransaction> findByToBinId(
            UUID toBinId);

    /**
     * Lookup transactions by reference number.
     */
    List<InventoryTransaction> findByReferenceNumber(
            String referenceNumber);

    /**
     * Lookup transactions by document type.
     */
    List<InventoryTransaction> findByReferenceType(
            String referenceType);

    /**
     * Lookup transactions by transaction type.
     */
    List<InventoryTransaction> findByTransactionType(
            TransactionType transactionType);
}