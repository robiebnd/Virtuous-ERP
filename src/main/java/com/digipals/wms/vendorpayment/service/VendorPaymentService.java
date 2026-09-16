package com.digipals.wms.vendorpayment.service;

import com.digipals.wms.vendorpayment.dto.CreateVendorPaymentRequest;
import com.digipals.wms.vendorpayment.entity.VendorPayment;
import java.util.List;
import java.util.UUID;

public interface VendorPaymentService {
    VendorPayment create(CreateVendorPaymentRequest request);
    VendorPayment approve(UUID id);
    VendorPayment pay(UUID id);
    List<VendorPayment> findAll();
    List<VendorPayment> findByInvoice(UUID invoiceId);
}
