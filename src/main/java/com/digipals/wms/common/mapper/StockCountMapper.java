package com.digipals.wms.common.mapper;

import com.digipals.wms.stockcount.dto.StockCountResponse;
import com.digipals.wms.stockcount.entity.StockCount;
import com.digipals.wms.stockcount.entity.StockCountLine;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class StockCountMapper {

    /**
     * Standard mapper when no lines are loaded or available.
     */
    public static StockCountResponse toResponse(StockCount entity) {
        return toResponse(entity, Collections.emptyList());
    }

    /**
     * Overloaded mapper when lines are provided explicitly.
     */
    public static StockCountResponse toResponse(StockCount entity, List<StockCountLine> lines) {
        if (entity == null) {
            return null;
        }

        List<StockCountLine> safeLines = (lines != null) ? lines : Collections.emptyList();

        // 1. Total lines loaded in this stock count
        int totalLines = safeLines.size();

        // 2. Lines where a physical count has been entered
        int countedLines = (int) safeLines.stream()
                .filter(line -> line.getCountedQuantity() != null)
                .count();

        // 3. Lines where there is a non-zero discrepancy
        int varianceLines = (int) safeLines.stream()
                .filter(line -> {
                    BigDecimal v = line.getVariance();
                    if (v == null && line.getCountedQuantity() != null && line.getSystemQuantity() != null) {
                        v = line.getCountedQuantity().subtract(line.getSystemQuantity());
                    }
                    return v != null && v.compareTo(BigDecimal.ZERO) != 0;
                })
                .count();

        // 4. Cumulative sum of all line variances
        BigDecimal totalVariance = safeLines.stream()
                .map(line -> {
                    BigDecimal v = line.getVariance();
                    if (v == null && line.getCountedQuantity() != null && line.getSystemQuantity() != null) {
                        return line.getCountedQuantity().subtract(line.getSystemQuantity());
                    }
                    return v != null ? v : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return StockCountResponse.builder()
                .id(entity.getId())
                .countNumber(entity.getCountNumber())
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null)
                .warehouseCode(entity.getWarehouse() != null ? entity.getWarehouse().getCode() : null)
                .warehouseName(entity.getWarehouse() != null ? entity.getWarehouse().getName() : null)
                .status(entity.getStatus())
                .remarks(entity.getRemarks())
                .createdAt(entity.getCreatedAt())
                .completedAt(entity.getCompletedAt())
                .totalLines(totalLines)
                .countedLines(countedLines)
                .varianceLines(varianceLines)
                .totalVariance(totalVariance)
                .build();
    }
}