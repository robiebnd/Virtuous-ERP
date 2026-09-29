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
    List<InventoryBin> findAll(); InventoryBin findById(UUID id);
    List<InventoryBin> findByWarehouse(UUID warehouseId); List<InventoryBin> findByProduct(UUID productId);
    InventoryBin adjustStock(UUID inventoryBinId, BigDecimal quantity);
    InventoryBin receiveStock(Warehouse warehouse,Bin bin,Product product,BigDecimal quantity,String referenceNumber,String referenceType,String remarks,User performedBy);
    InventoryBin receiveQualityStock(Warehouse warehouse,Bin bin,Product product,BigDecimal quantity,String referenceNumber,String referenceType,String remarks,User performedBy);
    InventoryBin releaseQualityStock(Warehouse warehouse,Bin bin,Product product,BigDecimal quantity,String referenceNumber,String referenceType,String remarks,User performedBy);
    InventoryBin blockQualityStock(Warehouse warehouse,Bin bin,Product product,BigDecimal quantity,String referenceNumber,String referenceType,String remarks,User performedBy);
    InventoryBin issueStock(Warehouse warehouse,Bin bin,Product product,BigDecimal quantity,String referenceNumber,String referenceType,String remarks,User performedBy);
    void moveStock(Warehouse warehouse,Bin fromBin,Bin toBin,Product product,BigDecimal quantity,String referenceNumber,String referenceType,String remarks,User performedBy);
    void transferStock(Warehouse sourceWarehouse,Bin fromBin,Warehouse destinationWarehouse,Bin toBin,Product product,BigDecimal quantity,String referenceNumber,String referenceType,String remarks,User performedBy);
    InventoryBin reserveStock(UUID inventoryBinId,BigDecimal quantity); InventoryBin releaseReservation(UUID inventoryBinId,BigDecimal quantity);
    BigDecimal availableStock(UUID inventoryBinId);
    boolean inventoryExists(UUID warehouseId,UUID binId,UUID productId);
    InventoryBin getInventory(UUID warehouseId,UUID binId,UUID productId);
}