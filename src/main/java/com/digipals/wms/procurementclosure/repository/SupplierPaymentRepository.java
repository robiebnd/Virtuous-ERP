package com.digipals.wms.procurementclosure.repository;
import com.digipals.wms.procurementclosure.entity.SupplierPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SupplierPaymentRepository extends JpaRepository<SupplierPayment, UUID> {
 List<SupplierPayment> findByInvoiceId(UUID invoiceId);
 Optional<SupplierPayment> findByPaymentNumber(String paymentNumber);
}
