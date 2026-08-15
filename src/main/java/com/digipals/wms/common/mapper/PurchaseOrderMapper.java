package com.digipals.wms.common.mapper;

import com.digipals.wms.purchaseorders.dto.PurchaseOrderLineResponse;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;

import java.math.RoundingMode;

public final class PurchaseOrderMapper {

    private PurchaseOrderMapper() {
    }

    public static PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        return PurchaseOrderResponse.builder()
                .id(purchaseOrder.getId())
                .poNumber(purchaseOrder.getPoNumber())
                .status(purchaseOrder.getStatus())
                .source(purchaseOrder.getSource())
                .supplierId(purchaseOrder.getSupplier() == null ? null : purchaseOrder.getSupplier().getId())
                .supplierCode(purchaseOrder.getSupplier() == null ? null : purchaseOrder.getSupplier().getCode())
                .supplierName(purchaseOrder.getSupplier() == null ? null : purchaseOrder.getSupplier().getName())
                .warehouseId(purchaseOrder.getWarehouse() == null ? null : purchaseOrder.getWarehouse().getId())
                .warehouseCode(purchaseOrder.getWarehouse() == null ? null : purchaseOrder.getWarehouse().getCode())
                .warehouseName(purchaseOrder.getWarehouse() == null ? null : purchaseOrder.getWarehouse().getName())
                .purchaseRequisitionId(purchaseOrder.getPurchaseRequisition() == null ? null : purchaseOrder.getPurchaseRequisition().getId())
                .purchaseRequisitionNumber(purchaseOrder.getPurchaseRequisition() == null ? null : purchaseOrder.getPurchaseRequisition().getRequisitionNumber())
                .createdById(purchaseOrder.getCreatedBy() == null ? null : purchaseOrder.getCreatedBy().getId())
                .createdBy(purchaseOrder.getCreatedBy() == null ? null : purchaseOrder.getCreatedBy().getUsername())
                .approvedById(purchaseOrder.getApprovedBy() == null ? null : purchaseOrder.getApprovedBy().getId())
                .approvedBy(purchaseOrder.getApprovedBy() == null ? null : purchaseOrder.getApprovedBy().getUsername())
                .active(purchaseOrder.getActive())
                .createdAt(purchaseOrder.getCreatedAt())
                .updatedAt(purchaseOrder.getUpdatedAt())
                .lines(purchaseOrderLineRepositoryNotAvailableInMapper(purchaseOrder))
                .build();
    }

    private static java.util.List<PurchaseOrderLineResponse> purchaseOrderLineRepositoryNotAvailableInMapper(PurchaseOrder purchaseOrder) {
        // The entity relationship is intentionally not exposed as a collection.
        // The service populates the response lines after persistence.
        return new java.util.ArrayList<>();
    }

    public static PurchaseOrderLineResponse toLineResponse(PurchaseOrderLine line) {
        if (line == null) {
            return null;
        }

        return PurchaseOrderLineResponse.builder()
                .id(line.getId())
                .productId(line.getProduct() == null ? null : line.getProduct().getId())
                .sku(line.getProduct() == null ? null : line.getProduct().getSku())
                .productName(line.getProduct() == null ? null : line.getProduct().getName())
                .quantity(scale(line.getQuantity()))
                .unitPrice(scale(line.getUnitPrice()))
                .lineTotal(scale(line.getLineTotal()))
                .build();
    }

    private static java.math.BigDecimal scale(java.math.BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}
