package com.digipals.wms.procurementclosure.repository;
import com.digipals.wms.procurementclosure.entity.SupplierInvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SupplierInvoiceLineRepository extends JpaRepository<SupplierInvoiceLine, UUID> {
 List<SupplierInvoiceLine> findByInvoiceId(UUID invoiceId);
 List<SupplierInvoiceLine> findByPurchaseOrderLineId(UUID purchaseOrderLineId);
}
