package com.digipals.wms.bintransfer.repository;

import com.digipals.wms.bintransfer.entity.BinTransfer;
import com.digipals.wms.bintransfer.entity.BinTransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinTransferRepository
        extends JpaRepository<BinTransfer, UUID> {

    Optional<BinTransfer> findByTransferNumber(
            String transferNumber);

    List<BinTransfer> findByWarehouseId(
            UUID warehouseId);

    List<BinTransfer> findByStatus(
            BinTransferStatus status);

    List<BinTransfer> findByWarehouseIdAndStatus(
            UUID warehouseId,
            BinTransferStatus status);

    List<BinTransfer> findByTransferDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate);

    List<BinTransfer> findByWarehouseIdAndTransferDateBetween(
            UUID warehouseId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    boolean existsByTransferNumber(
            String transferNumber);
}