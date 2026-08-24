package com.digipals.wms.common.mapper;

import com.digipals.wms.goodsreceiving.dto.GoodsReceiptLineResponse;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;

import java.math.BigDecimal;

public final class GoodsReceiptLineMapper {

    private GoodsReceiptLineMapper() {
    }

    public static GoodsReceiptLineResponse toResponse(
            GoodsReceiptLine line) {

        if (line == null) {
            return null;
        }

        PurchaseOrderLine poLine = line.getPurchaseOrderLine();

        BigDecimal previouslyReceivedQuantity = poLine == null || poLine.getReceivedQuantity() == null
                ? BigDecimal.ZERO
                : poLine.getReceivedQuantity();

        BigDecimal outstandingQuantity = poLine == null || poLine.getOutstandingQuantity() == null
                ? BigDecimal.ZERO
                : poLine.getOutstandingQuantity();

        return GoodsReceiptLineResponse.builder()

                .id(line.getId())

                .purchaseOrderLineId(
                        poLine == null
                                ? null
                                : poLine.getId())

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

                .orderedQuantity(line.getOrderedQuantity())

                .previouslyReceivedQuantity(previouslyReceivedQuantity)

                .outstandingQuantity(outstandingQuantity)

                .receivedQuantity(line.getReceivedQuantity())

                .acceptedQuantity(line.getAcceptedQuantity())

                .rejectedQuantity(line.getRejectedQuantity())

                .unitCost(line.getUnitCost())

                .remarks(line.getRemarks())

                .build();
    }
}