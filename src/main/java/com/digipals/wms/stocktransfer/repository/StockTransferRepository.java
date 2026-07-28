package com.digipals.wms.stocktransfer.repository;



import com.digipals.wms.stocktransfer.entity.StockTransfer;
import com.digipals.wms.stocktransfer.entity.StockTransferLine;
import com.digipals.wms.stocktransfer.entity.StockTransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockTransferRepository
        extends JpaRepository<StockTransfer, UUID> {

    Optional<StockTransfer>
    findByTransferNumber(
            String transferNumber);

    boolean existsByTransferNumber(
            String transferNumber);

    List<StockTransfer>
    findByStatus(
            StockTransferStatus status);

    List<StockTransfer>
    findBySourceWarehouseId(
            UUID warehouseId);

    List<StockTransfer>
    findByDestinationWarehouseId(
            UUID warehouseId);
}