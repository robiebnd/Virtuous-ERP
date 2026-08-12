package com.digipals.wms.inventory.reconciliation.service;

import com.digipals.wms.inventory.reconciliation.dto.InventoryReconciliationRequest;
import com.digipals.wms.inventorybin.entity.InventoryBin;

public interface InventoryReconciliationService {

    InventoryBin reconcile(InventoryReconciliationRequest request);
}
