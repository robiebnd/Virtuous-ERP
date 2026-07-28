package com.digipals.wms.purchaserequisition.repository;

import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseRequisitionLineRepository
        extends JpaRepository<PurchaseRequisitionLine, UUID> {

    List<PurchaseRequisitionLine> findByPurchaseRequisitionId(
            UUID purchaseRequisitionId);

    boolean existsByPurchaseRequisitionIdAndProductId(
            UUID purchaseRequisitionId,
            UUID productId);

    long countByPurchaseRequisitionId(
            UUID purchaseRequisitionId);

    void deleteByPurchaseRequisitionId(
            UUID purchaseRequisitionId);
}