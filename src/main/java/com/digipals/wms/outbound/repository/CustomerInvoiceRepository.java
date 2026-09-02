package com.digipals.wms.outbound.repository;
import com.digipals.wms.outbound.entity.CustomerInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, UUID> {
    Optional<CustomerInvoice> findByInvoiceNumber(String invoiceNumber);
    List<CustomerInvoice> findByCustomerId(UUID customerId);
}
