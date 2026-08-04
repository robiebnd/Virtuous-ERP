package com.digipals.wms.putaway.repository;

import com.digipals.wms.putaway.entity.PutAway;
import com.digipals.wms.putaway.entity.PutAwayStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PutAwayRepository
        extends JpaRepository<PutAway, UUID> {

    Optional<PutAway> findByPutAwayNumber(
            String putAwayNumber);

    boolean existsByPutAwayNumber(
            String putAwayNumber);

    List<PutAway> findByWarehouseId(
            UUID warehouseId);

    List<PutAway> findByGoodsReceiptId(
            UUID goodsReceiptId);

    List<PutAway> findByStatus(
            PutAwayStatus status);

    boolean existsByGoodsReceiptIdAndStatusNot(
            UUID goodsReceiptId,
            PutAwayStatus status);
}