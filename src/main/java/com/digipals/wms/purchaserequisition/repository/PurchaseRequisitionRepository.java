package com.digipals.wms.purchaserequisition.repository;

import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRequisitionRepository
        extends JpaRepository<PurchaseRequisition, UUID> {

    Optional<PurchaseRequisition> findByRequisitionNumber(
            String requisitionNumber);

    boolean existsByRequisitionNumber(
            String requisitionNumber);

    List<PurchaseRequisition> findByStatus(
            PurchaseRequisitionStatus status);

    List<PurchaseRequisition> findByWarehouseId(
            UUID warehouseId);
}