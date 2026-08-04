package com.digipals.wms.bin.repository;

import com.digipals.wms.bin.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinRepository
        extends JpaRepository<Bin, UUID> {

    List<Bin> findByWarehouseId(UUID warehouseId);

    Optional<Bin> findByWarehouseIdAndCode(
            UUID warehouseId,
            String code);

    boolean existsByWarehouseIdAndCode(
            UUID warehouseId,
            String code);

    Optional<Bin> findByWarehouseIdAndReceivingBinTrue(UUID warehouseId);
  
}
