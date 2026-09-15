package com.digipals.wms.vendorpayment.repository;

import com.digipals.wms.vendorpayment.entity.VendorPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface VendorPaymentRepository extends JpaRepository<VendorPayment, UUID> {
    List<VendorPayment> findAllByVendorInvoiceIdOrderByPaymentDateDesc(UUID vendorInvoiceId);
}
