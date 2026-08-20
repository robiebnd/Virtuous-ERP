package com.digipals.wms.purchasinginforecord.repository;

import com.digipals.wms.purchasinginforecord.entity.PurchasingInfoRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchasingInfoRecordRepository extends JpaRepository<PurchasingInfoRecord, UUID> {

    Optional<PurchasingInfoRecord> findBySupplierProductIdAndWarehouseId(
            UUID supplierProductIdentifierId,
            UUID warehouseId);

    List<PurchasingInfoRecord> findBySupplierProductId(UUID supplierProductIdentifierId);

    List<PurchasingInfoRecord> findByWarehouseId(UUID warehouseId);
}
