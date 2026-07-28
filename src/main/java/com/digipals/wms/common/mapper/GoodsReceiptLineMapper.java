package com.digipals.wms.common.mapper;

import com.digipals.wms.goodsreceiving.dto.GoodsReceiptLineResponse;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;

public final class GoodsReceiptLineMapper {

    private GoodsReceiptLineMapper() {
    }

    public static GoodsReceiptLineResponse toResponse(
            GoodsReceiptLine line) {

        if (line == null) {
            return null;
        }

        return GoodsReceiptLineResponse.builder()

                .id(line.getId())

                .purchaseOrderLineId(
                        line.getPurchaseOrderLine() == null
                                ? null
                                : line.getPurchaseOrderLine().getId())

                .productId(
                        line.getProduct() == null
                                ? null
                                : line.getProduct().getId())

                .sku(
                        line.getProduct() == null
                                ? null
                                : line.getProduct().getSku())

                .productName(
                        line.getProduct() == null
                                ? null
                                : line.getProduct().getName())

                .orderedQuantity(
                        line.getOrderedQuantity())

                .receivedQuantity(
                        line.getReceivedQuantity())

                .acceptedQuantity(
                        line.getAcceptedQuantity())

                .rejectedQuantity(
                        line.getRejectedQuantity())

                .unitCost(
                        line.getUnitCost())

                .remarks(
                        line.getRemarks())

                .build();
    }
}