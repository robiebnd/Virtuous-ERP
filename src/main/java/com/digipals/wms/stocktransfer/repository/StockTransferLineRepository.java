package com.digipals.wms.stocktransfer.repository;

import com.digipals.wms.stocktransfer.entity.StockTransferLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockTransferLineRepository
        extends JpaRepository<StockTransferLine, UUID> {

    

    List<StockTransferLine>
    findByStockTransferId(
            UUID stockTransferId);

    List<StockTransferLine>
    findByProductId(
            UUID productId);
}