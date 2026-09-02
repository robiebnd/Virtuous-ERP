package com.digipals.wms.procurementclosure.repository;
import com.digipals.wms.procurementclosure.entity.SupplierInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SupplierInvoiceRepository extends JpaRepository<SupplierInvoice, UUID> {
 Optional<SupplierInvoice> findByInvoiceNumber(String invoiceNumber);
 List<SupplierInvoice> findByPurchaseOrderId(UUID purchaseOrderId);
 List<SupplierInvoice> findBySupplierId(UUID supplierId);
 boolean existsByInvoiceNumber(String invoiceNumber);
}
