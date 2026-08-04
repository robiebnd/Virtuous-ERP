package com.digipals.wms.common.mapper;

import com.digipals.wms.inventorybin.dto.InventoryBinResponse;
import com.digipals.wms.inventorybin.entity.InventoryBin;

import java.math.BigDecimal;

public final class InventoryBinMapper {

    private InventoryBinMapper() {
    }

    public static InventoryBinResponse toResponse(
            InventoryBin inventoryBin) {

        if (inventoryBin == null) {
            return null;
        }

        BigDecimal available =
                inventoryBin.getQuantityOnHand()
                        .subtract(inventoryBin.getQuantityReserved());

        return InventoryBinResponse.builder()

                /*
                 * Identity
                 */
                .id(inventoryBin.getId())

                /*
                 * Warehouse
                 */
                .warehouseId(
                        inventoryBin.getWarehouse() != null
                                ? inventoryBin.getWarehouse().getId()
                                : null)

                .warehouseCode(
                        inventoryBin.getWarehouse() != null
                                ? inventoryBin.getWarehouse().getCode()
                                : null)

                .warehouseName(
                        inventoryBin.getWarehouse() != null
                                ? inventoryBin.getWarehouse().getName()
                                : null)

                /*
                 * Bin
                 */
                .binId(
                        inventoryBin.getBin() != null
                                ? inventoryBin.getBin().getId()
                                : null)

                .binCode(
                        inventoryBin.getBin() != null
                                ? inventoryBin.getBin().getCode()
                                : null)

                .binName(
                        inventoryBin.getBin() != null
                                ? inventoryBin.getBin().getName()
                                : null)

                /*
                 * Product
                 */
                .productId(
                        inventoryBin.getProduct() != null
                                ? inventoryBin.getProduct().getId()
                                : null)

                .sku(
                        inventoryBin.getProduct() != null
                                ? inventoryBin.getProduct().getSku()
                                : null)

                .productName(
                        inventoryBin.getProduct() != null
                                ? inventoryBin.getProduct().getName()
                                : null)

                /*
                 * Stock
                 */
                .quantityOnHand(
                        inventoryBin.getQuantityOnHand())

                .quantityReserved(
                        inventoryBin.getQuantityReserved())

                .quantityAvailable(
                        available)

                /*
                 * Audit
                 */
                .active(
                        inventoryBin.getActive())

                .createdAt(
                        inventoryBin.getCreatedAt())

                .updatedAt(
                        inventoryBin.getUpdatedAt())

                .build();
    }
}