package com.digipals.wms.common.mapper;

import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;

public final class PurchaseOrderMapper {

    private PurchaseOrderMapper() {
    }

    public static PurchaseOrderResponse toResponse(
            PurchaseOrder purchaseOrder) {

        if (purchaseOrder == null) {
            return null;
        }

        return PurchaseOrderResponse.builder()

                .id(purchaseOrder.getId())

                .poNumber(purchaseOrder.getPoNumber())

                .status(purchaseOrder.getStatus())

                .source(purchaseOrder.getSource())

                .supplierId(
                        purchaseOrder.getSupplier() == null
                                ? null
                                : purchaseOrder.getSupplier().getId())

                .supplierCode(
                        purchaseOrder.getSupplier() == null
                                ? null
                                : purchaseOrder.getSupplier().getCode())

                .supplierName(
                        purchaseOrder.getSupplier() == null
                                ? null
                                : purchaseOrder.getSupplier().getName())

                .warehouseId(
                        purchaseOrder.getWarehouse() == null
                                ? null
                                : purchaseOrder.getWarehouse().getId())

                .warehouseCode(
                        purchaseOrder.getWarehouse() == null
                                ? null
                                : purchaseOrder.getWarehouse().getCode())

                .warehouseName(
                        purchaseOrder.getWarehouse() == null
                                ? null
                                : purchaseOrder.getWarehouse().getName())

                .purchaseRequisitionId(
                        purchaseOrder.getPurchaseRequisition() == null
                                ? null
                                : purchaseOrder.getPurchaseRequisition().getId())

                .purchaseRequisitionNumber(
                purchaseOrder.getPurchaseRequisition() == null
                ? null
                : purchaseOrder.getPurchaseRequisition().getRequisitionNumber())

                .createdById(
                        purchaseOrder.getCreatedBy() == null
                                ? null
                                : purchaseOrder.getCreatedBy().getId())

                .createdBy(
                        purchaseOrder.getCreatedBy() == null
                                ? null
                                : purchaseOrder.getCreatedBy().getUsername())

                .approvedById(
                        purchaseOrder.getApprovedBy() == null
                                ? null
                                : purchaseOrder.getApprovedBy().getId())

                .approvedBy(
                        purchaseOrder.getApprovedBy() == null
                                ? null
                                : purchaseOrder.getApprovedBy().getUsername())

                .active(purchaseOrder.getActive())

                .createdAt(purchaseOrder.getCreatedAt())

                .updatedAt(purchaseOrder.getUpdatedAt())

                .build();
    }
}