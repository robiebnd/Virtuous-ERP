package com.digipals.wms.vendorinvoice.repository;

import com.digipals.wms.vendorinvoice.entity.VendorInvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VendorInvoiceLineRepository extends JpaRepository<VendorInvoiceLine, UUID> {
}
