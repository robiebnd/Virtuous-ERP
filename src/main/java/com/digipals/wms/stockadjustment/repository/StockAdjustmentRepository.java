package com.digipals.wms.stockadjustment.repository;

import com.digipals.wms.stockadjustment.entity.AdjustmentStatus;
import com.digipals.wms.stockadjustment.entity.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockAdjustmentRepository
        extends JpaRepository<StockAdjustment, UUID> {

    boolean existsByAdjustmentNumber(
            String adjustmentNumber);

    Optional<StockAdjustment> findByAdjustmentNumber(
            String adjustmentNumber);

    List<StockAdjustment> findByWarehouseId(
            UUID warehouseId);

    List<StockAdjustment> findByStatus(
            AdjustmentStatus status);

    long countByStatus(
            AdjustmentStatus status);
}