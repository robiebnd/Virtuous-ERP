package com.digipals.wms.vendorinvoice.repository;

import com.digipals.wms.vendorinvoice.entity.VendorInvoice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendorInvoiceRepository extends JpaRepository<VendorInvoice, UUID> {
    boolean existsBySupplierIdAndSupplierInvoiceNumber(UUID supplierId, String supplierInvoiceNumber);
    Optional<VendorInvoice> findByInvoiceNumber(String invoiceNumber);
    @EntityGraph(attributePaths={"lines","supplier","purchaseOrder","goodsReceipt"})
    Optional<VendorInvoice> findWithLinesById(UUID id);
    @EntityGraph(attributePaths={"lines","supplier","purchaseOrder","goodsReceipt"})
    List<VendorInvoice> findAllByOrderByInvoiceDateDesc();
}
