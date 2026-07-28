package com.digipals.wms.common.mapper;

import com.digipals.wms.stocktransfer.dto.StockTransferLineResponse;
import com.digipals.wms.stocktransfer.entity.StockTransferLine;

public final class StockTransferLineMapper {

    private StockTransferLineMapper() {
    }

    public static StockTransferLineResponse toResponse(
            StockTransferLine line) {

        return StockTransferLineResponse.builder()

                .id(
                        line.getId())

                .transferId(
                        line.getStockTransfer().getId())

                .productId(
                        line.getProduct().getId())

                .sku(
                        line.getProduct().getSku())

                .productName(
                        line.getProduct().getName())

                .quantity(
                        line.getQuantity())

                .build();
    }
}