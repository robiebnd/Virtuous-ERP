package com.digipals.wms.stockcount.repository;

import com.digipals.wms.stockcount.entity.StockCountLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockCountLineRepository
        extends JpaRepository<StockCountLine, UUID> {

    List<StockCountLine> findByStockCountId(
            UUID stockCountId);

    boolean existsByStockCountIdAndProductIdAndBinId(
        UUID stockCountId,
        UUID productId,
        UUID binId);
}