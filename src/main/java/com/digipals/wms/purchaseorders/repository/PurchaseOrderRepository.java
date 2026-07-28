package com.digipals.wms.purchaseorders.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder>
    findByPoNumber(String poNumber);
}
