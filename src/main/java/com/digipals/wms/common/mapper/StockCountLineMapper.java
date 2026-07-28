package com.digipals.wms.common.mapper;

import com.digipals.wms.stockcount.dto.StockCountLineResponse;
import com.digipals.wms.stockcount.entity.StockCountLine;

import java.math.BigDecimal;

public class StockCountLineMapper {

    private StockCountLineMapper() {
    }

    public static StockCountLineResponse toResponse(StockCountLine entity) {
        if (entity == null) {
            return null;
        }

        // Calculate variance on the fly if it's null in the database but counts exist
        BigDecimal variance = entity.getVariance();
        if (variance == null && entity.getCountedQuantity() != null && entity.getSystemQuantity() != null) {
            variance = entity.getCountedQuantity().subtract(entity.getSystemQuantity());
        }

        return StockCountLineResponse.builder()
                .id(entity.getId())
                .stockCountId(entity.getStockCount() != null ? entity.getStockCount().getId() : null)
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .sku(entity.getProduct() != null ? entity.getProduct().getSku() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getName() : null)
                .systemQuantity(entity.getSystemQuantity())
                .countedQuantity(entity.getCountedQuantity())
                .variance(variance)
                .reason(entity.getReason())
                .build();
    }
}