package com.digipals.wms.common.mapper;

import com.digipals.wms.goodsreceiving.dto.GoodsReceiptResponse;
import com.digipals.wms.goodsreceiving.entity.GoodsReceipt;

public final class GoodsReceiptMapper {

    private GoodsReceiptMapper() {
    }

    public static GoodsReceiptResponse toResponse(
            GoodsReceipt goodsReceipt) {

        if (goodsReceipt == null) {
            return null;
        }

        return GoodsReceiptResponse.builder()

                .id(goodsReceipt.getId())

                .grnNumber(goodsReceipt.getGrnNumber())

                .status(goodsReceipt.getStatus())

                .purchaseOrderId(
                        goodsReceipt.getPurchaseOrder() == null
                                ? null
                                : goodsReceipt.getPurchaseOrder().getId())

                .purchaseOrderNumber(
                        goodsReceipt.getPurchaseOrder() == null
                                ? null
                                : goodsReceipt.getPurchaseOrder().getPoNumber())

                .warehouseId(
                        goodsReceipt.getWarehouse() == null
                                ? null
                                : goodsReceipt.getWarehouse().getId())

                .warehouseCode(
                        goodsReceipt.getWarehouse() == null
                                ? null
                                : goodsReceipt.getWarehouse().getCode())

                .warehouseName(
                        goodsReceipt.getWarehouse() == null
                                ? null
                                : goodsReceipt.getWarehouse().getName())

                .receivedById(
                        goodsReceipt.getReceivedBy() == null
                                ? null
                                : goodsReceipt.getReceivedBy().getId())

                .receivedBy(
                        goodsReceipt.getReceivedBy() == null
                                ? null
                                : goodsReceipt.getReceivedBy().getUsername())

                .approvedById(
                        goodsReceipt.getApprovedBy() == null
                                ? null
                                : goodsReceipt.getApprovedBy().getId())

                .approvedBy(
                        goodsReceipt.getApprovedBy() == null
                                ? null
                                : goodsReceipt.getApprovedBy().getUsername())

                .supplierDeliveryNote(
                        goodsReceipt.getSupplierDeliveryNote())

                .remarks(
                        goodsReceipt.getRemarks())

                .receivedDate(
                        goodsReceipt.getReceivedDate())

                .approvedAt(
                        goodsReceipt.getApprovedAt())

                .active(
                        goodsReceipt.getActive())

                .createdAt(
                        goodsReceipt.getCreatedAt())

                .updatedAt(
                        goodsReceipt.getUpdatedAt())

                .build();
    }
}