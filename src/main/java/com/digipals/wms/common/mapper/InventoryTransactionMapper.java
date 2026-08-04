package com.digipals.wms.common.mapper;

import com.digipals.wms.inventorybin.entity.InventoryBin;
import com.digipals.wms.inventorytransaction.dto.InventoryTransactionResponse;
import com.digipals.wms.inventorytransaction.entity.InventoryTransaction;

public final class InventoryTransactionMapper {

    private InventoryTransactionMapper() {
    }

    public static InventoryTransactionResponse toResponse(
            InventoryTransaction transaction) {

        if (transaction == null) {
            return null;
        }

        InventoryBin inventoryBin = transaction.getInventoryBin();

        return InventoryTransactionResponse.builder()

                .id(transaction.getId())

                .inventoryBinId(
                        inventoryBin == null
                                ? null
                                : inventoryBin.getId())

                .warehouseId(
                        inventoryBin == null
                                ? null
                                : inventoryBin.getWarehouse().getId())

                .warehouseCode(
                        inventoryBin == null
                                ? null
                                : inventoryBin.getWarehouse().getCode())

                .warehouseName(
                        inventoryBin == null
                                ? null
                                : inventoryBin.getWarehouse().getName())

                .binId(
                        inventoryBin == null
                                ? null
                                : inventoryBin.getBin().getId())

                .binCode(
                        inventoryBin == null
                                ? null
                                : inventoryBin.getBin().getCode())

                .productId(
                        inventoryBin == null
                                ? null
                                : inventoryBin.getProduct().getId())

                .sku(
                        inventoryBin == null
                                ? null
                                : inventoryBin.getProduct().getSku())

                .productName(
                        inventoryBin == null
                                ? null
                                : inventoryBin.getProduct().getName())

                .transactionType(
                        transaction.getTransactionType())

                .quantity(
                        transaction.getQuantity())

                .balanceAfter(
                        transaction.getBalanceAfter())

                .referenceNumber(
                        transaction.getReferenceNumber())

                .referenceType(
                        transaction.getReferenceType())

                .performedById(
                        transaction.getPerformedBy() == null
                                ? null
                                : transaction.getPerformedBy().getId())

                .performedBy(
                        transaction.getPerformedBy() == null
                                ? null
                                : transaction.getPerformedBy().getUsername())

                .fromBinId(
                        transaction.getFromBin() == null
                                ? null
                                : transaction.getFromBin().getId())

                .fromBinCode(
                        transaction.getFromBin() == null
                                ? null
                                : transaction.getFromBin().getCode())

                .toBinId(
                        transaction.getToBin() == null
                                ? null
                                : transaction.getToBin().getId())

                .toBinCode(
                        transaction.getToBin() == null
                                ? null
                                : transaction.getToBin().getCode())

                .remarks(
                        transaction.getRemarks())

                .active(
                        transaction.getActive())

                .createdAt(
                        transaction.getCreatedAt())

                .updatedAt(
                        transaction.getUpdatedAt())

                .build();
    }
}