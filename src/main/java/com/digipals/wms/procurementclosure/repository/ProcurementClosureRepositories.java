package com.digipals.wms.procurementclosure.repository;

import com.digipals.wms.procurementclosure.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SupplierInvoiceRepository extends JpaRepository<SupplierInvoice, UUID> {
    Optional<SupplierInvoice> findByInvoiceNumber(String invoiceNumber);
    List<SupplierInvoice> findByPurchaseOrderId(UUID purchaseOrderId);
    List<SupplierInvoice> findBySupplierId(UUID supplierId);
    boolean existsByInvoiceNumber(String invoiceNumber);
}

interface SupplierInvoiceLineRepository extends JpaRepository<SupplierInvoiceLine, UUID> {
    List<SupplierInvoiceLine> findByInvoiceId(UUID invoiceId);
    List<SupplierInvoiceLine> findByPurchaseOrderLineId(UUID purchaseOrderLineId);
}

interface SupplierPaymentRepository extends JpaRepository<SupplierPayment, UUID> {
    List<SupplierPayment> findByInvoiceId(UUID invoiceId);
    Optional<SupplierPayment> findByPaymentNumber(String paymentNumber);
}

interface VendorEvaluationRepository extends JpaRepository<VendorEvaluation, UUID> {
    List<VendorEvaluation> findBySupplierId(UUID supplierId);
    List<VendorEvaluation> findByPurchaseOrderId(UUID purchaseOrderId);
}
