package com.digipals.wms.inventorybin.service;

import com.digipals.wms.inventorybin.dto.CreateInventoryBinRequest;
import com.digipals.wms.inventorybin.dto.InventoryBinResponse;
import com.digipals.wms.inventorybin.dto.UpdateInventoryBinRequest;

import java.util.List;
import java.util.UUID;

public interface InventoryBinService {

    InventoryBinResponse create(CreateInventoryBinRequest request);

    InventoryBinResponse update(
            UUID id,
            UpdateInventoryBinRequest request);

    InventoryBinResponse findById(UUID id);

    List<InventoryBinResponse> findAll();

    List<InventoryBinResponse> findByWarehouse(UUID warehouseId);

    List<InventoryBinResponse> findByBin(UUID binId);

    List<InventoryBinResponse> findByProduct(UUID productId);

    void delete(UUID id);
}
