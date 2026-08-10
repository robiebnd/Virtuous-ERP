package com.digipals.wms.purchaseorders.repository;

import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    boolean existsByPurchaseRequisitionId(UUID purchaseRequisitionId);

    Optional<PurchaseOrder> findByPurchaseRequisitionId(UUID purchaseRequisitionId);
}
