package com.digipals.wms.supplierquotation.repository;

import com.digipals.wms.supplierquotation.entity.SupplierQuotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupplierQuotationRepository extends JpaRepository<SupplierQuotation, UUID> {

    List<SupplierQuotation> findByPurchaseRequisitionIdOrderByCreatedAtDesc(UUID purchaseRequisitionId);

    List<SupplierQuotation> findByQuotationNumber(String quotationNumber);
}
