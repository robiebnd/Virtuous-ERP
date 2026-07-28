package com.digipals.wms.common.mapper;

import com.digipals.wms.inventory.dto.InventoryResponse;
import com.digipals.wms.inventory.entity.Inventory;

public class InventoryMapper {

    private InventoryMapper() {
    }

    public static InventoryResponse toResponse(
            Inventory inventory) {

        return InventoryResponse.builder()
                .id(inventory.getId())

                .warehouseCode(
                        inventory.getWarehouse().getCode())

                .warehouseName(
                        inventory.getWarehouse().getName())

                .productSku(
                        inventory.getProduct().getSku())

                .productName(
                        inventory.getProduct().getName())

                .quantityOnHand(
                        inventory.getQuantityOnHand())

                .quantityReserved(
                        inventory.getQuantityReserved())

                .reorderLevel(
                        inventory.getReorderLevel())

                .build();
    }
}