package com.digipals.wms.inventorytransaction.repository;

import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, UUID> {

    List<InventoryTransaction>
    findByInventoryId(UUID inventoryId);
}