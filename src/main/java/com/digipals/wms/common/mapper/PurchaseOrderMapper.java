package com.digipals.wms.common.mapper;

import com.digipals.wms.purchaseorders.dto.PurchaseOrderLineResponse;
import com.digipals.wms.purchaseorders.dto.PurchaseOrderResponse;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public final class PurchaseOrderMapper {

    private PurchaseOrderMapper() {
    }

    public static PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        List<PurchaseOrderLineResponse> lines = purchaseOrder.getLines() == null
                ? Collections.emptyList()
                : purchaseOrder.getLines().stream()
                .map(PurchaseOrderMapper::toLineResponse)
                .toList();

        return PurchaseOrderResponse.builder()
                .id(purchaseOrder.getId())
                .poNumber(purchaseOrder.getPoNumber())
                .status(purchaseOrder.getStatus())
                .source(purchaseOrder.getSource())
                .currency(purchaseOrder.getCurrency())
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
                .approvedAt(purchaseOrder.getApprovedAt())
                .active(purchaseOrder.getActive())
                .createdAt(purchaseOrder.getCreatedAt())
                .updatedAt(purchaseOrder.getUpdatedAt())
                .lines(lines)
                .build();
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

    private static BigDecimal scale(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}
