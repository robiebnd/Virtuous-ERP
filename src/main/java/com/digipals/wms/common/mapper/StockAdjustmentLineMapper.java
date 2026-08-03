package com.digipals.wms.common.mapper;

import com.digipals.wms.stockadjustment.dto.StockAdjustmentLineResponse;
import com.digipals.wms.stockadjustment.entity.StockAdjustmentLine;

public final class StockAdjustmentLineMapper {

    private StockAdjustmentLineMapper() {
    }

    public static StockAdjustmentLineResponse toResponse(
            StockAdjustmentLine line) {

        return StockAdjustmentLineResponse.builder()

                .id(line.getId())

                .adjustmentId(
                        line.getStockAdjustment().getId())

                .productId(
                        line.getProduct().getId())

                .sku(
                        line.getProduct().getSku())

                .productName(
                        line.getProduct().getName())

                .binId(
                        line.getBin().getId())

                .binCode(
                        line.getBin().getCode())

                .systemQuantity(
                        line.getSystemQuantity())

                .countedQuantity(
                        line.getCountedQuantity())

                .difference(
                        line.getDifference())

                .adjustmentQuantity(
                        line.getAdjustmentQuantity())

                .build();
    }
}