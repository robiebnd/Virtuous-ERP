package com.digipals.wms.purchaseorders.repository;

import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderLineRepository
        extends JpaRepository<PurchaseOrderLine, UUID> {

    

    List<PurchaseOrderLine>
        findByPurchaseOrderId(UUID purchaseOrderId);

    boolean existsByPurchaseOrderIdAndProductId(
        UUID purchaseOrderId,
        UUID productId);

    }