package com.digipals.wms.stockcount.repository;

import com.digipals.wms.stockcount.entity.StockCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockCountRepository
        extends JpaRepository<StockCount, UUID> {

    Optional<StockCount>
    findByCountNumber(
            String countNumber);

Optional<StockCount> findByStockAdjustmentId(UUID stockAdjustmentId);

}