package com.digipals.wms.bintransfer.repository;

import com.digipals.wms.bintransfer.entity.BinTransferLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinTransferLineRepository
        extends JpaRepository<BinTransferLine, UUID> {

    List<BinTransferLine> findByBinTransferId(
            UUID binTransferId);

    List<BinTransferLine> findByProductId(
            UUID productId);

    List<BinTransferLine> findByBinTransferIdAndProductId(
            UUID binTransferId,
            UUID productId);

    Optional<BinTransferLine> findByBinTransferIdAndId(
            UUID binTransferId,
            UUID id);

    boolean existsByBinTransferIdAndProductId(
            UUID binTransferId,
            UUID productId);

    long countByBinTransferId(
            UUID binTransferId);

    void deleteByBinTransferId(
            UUID binTransferId);
}