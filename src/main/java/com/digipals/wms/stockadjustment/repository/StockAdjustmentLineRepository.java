package com.digipals.wms.stockadjustment.repository;

import com.digipals.wms.stockadjustment.entity.StockAdjustmentLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockAdjustmentLineRepository
        extends JpaRepository<StockAdjustmentLine, UUID> {

    List<StockAdjustmentLine> findByStockAdjustmentId(
            UUID stockAdjustmentId);

    List<StockAdjustmentLine> findByProductId(
            UUID productId);

    boolean existsByStockAdjustmentIdAndProductId(
            UUID adjustmentId,
            UUID productId);

    void deleteByStockAdjustmentId(
            UUID adjustmentId);

    long countByStockAdjustmentId(
            UUID adjustmentId);
}