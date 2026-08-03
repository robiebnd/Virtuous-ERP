package com.digipals.wms.common.mapper;

import com.digipals.wms.inventorybin.dto.InventoryBinResponse;
import com.digipals.wms.inventorybin.entity.InventoryBin;

public class InventoryBinMapper {

    public static InventoryBinResponse toResponse(
            InventoryBin entity) {

        if (entity == null) {
            return null;
        }

        return InventoryBinResponse.builder()

                .id(entity.getId())

                .warehouseId(entity.getWarehouse().getId())
                .warehouseCode(entity.getWarehouse().getCode())
                .warehouseName(entity.getWarehouse().getName())

                .binId(entity.getBin().getId())
                .binCode(entity.getBin().getCode())
                .binName(entity.getBin().getName())
                .binType(entity.getBin().getType())

                .productId(entity.getProduct().getId())
                .sku(entity.getProduct().getSku())
                .productName(entity.getProduct().getName())

                .quantityOnHand(entity.getQuantityOnHand())
                .quantityReserved(entity.getQuantityReserved())

                .active(entity.getActive())

                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())

                .build();
    }
}
