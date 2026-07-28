package com.digipals.wms.common.mapper;

import com.digipals.wms.stockadjustment.dto.StockAdjustmentResponse;
import com.digipals.wms.stockadjustment.entity.StockAdjustment;

public final class StockAdjustmentMapper {

    private StockAdjustmentMapper() {
    }

    public static StockAdjustmentResponse toResponse(
            StockAdjustment adjustment) {

        return StockAdjustmentResponse.builder()

                .id(
                        adjustment.getId())

                .adjustmentNumber(
                        adjustment.getAdjustmentNumber())

                .warehouseId(
                        adjustment.getWarehouse().getId())

                .warehouseCode(
                        adjustment.getWarehouse().getCode())

                .warehouseName(
                        adjustment.getWarehouse().getName())

                .status(
                        adjustment.getStatus())

                .reason(
                        adjustment.getReason())

                .remarks(
                        adjustment.getRemarks())

                .createdAt(
                        adjustment.getCreatedAt())

                .postedAt(
                        adjustment.getPostedAt())

                .build();
    }
}