package com.digipals.wms.procurementclosure.repository;
import com.digipals.wms.procurementclosure.entity.VendorEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface VendorEvaluationRepository extends JpaRepository<VendorEvaluation, UUID> {
 List<VendorEvaluation> findBySupplierId(UUID supplierId);
 List<VendorEvaluation> findByPurchaseOrderId(UUID purchaseOrderId);
}
