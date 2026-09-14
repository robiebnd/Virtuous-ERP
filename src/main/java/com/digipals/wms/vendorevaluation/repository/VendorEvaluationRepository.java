package com.digipals.wms.vendorevaluation.repository;

import com.digipals.wms.vendorevaluation.entity.VendorEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface VendorEvaluationRepository extends JpaRepository<VendorEvaluation, UUID> {
    List<VendorEvaluation> findAllBySupplierIdOrderByEvaluationDateDesc(UUID supplierId);
    boolean existsByPurchaseOrderId(UUID purchaseOrderId);
}
