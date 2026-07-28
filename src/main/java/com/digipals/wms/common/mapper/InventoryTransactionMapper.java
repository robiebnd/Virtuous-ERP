package com.digipals.wms.common.mapper;


import com.digipals.wms.inventory.entity.Inventory;
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

        Inventory inventory = transaction.getInventory();

        return InventoryTransactionResponse.builder()

                .id(transaction.getId())

                .inventoryId(
                        inventory == null
                                ? null
                                : inventory.getId())

                .warehouseId(
                        inventory == null
                                ? null
                                : inventory.getWarehouse().getId())

                .warehouseCode(
                        inventory == null
                                ? null
                                : inventory.getWarehouse().getCode())

                .warehouseName(
                        inventory == null
                                ? null
                                : inventory.getWarehouse().getName())

                .productId(
                        inventory == null
                                ? null
                                : inventory.getProduct().getId())

                .sku(
                        inventory == null
                                ? null
                                : inventory.getProduct().getSku())

                .productName(
                        inventory == null
                                ? null
                                : inventory.getProduct().getName())

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