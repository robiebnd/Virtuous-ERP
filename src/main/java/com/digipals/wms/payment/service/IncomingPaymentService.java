package com.digipals.wms.payment.service;

import com.digipals.wms.payment.dto.CreateIncomingPaymentRequest;
import com.digipals.wms.payment.entity.IncomingPayment;

import java.util.List;
import java.util.UUID;

public interface IncomingPaymentService {
    IncomingPayment receive(CreateIncomingPaymentRequest request);
    IncomingPayment findById(UUID id);
    List<IncomingPayment> findAll();
    List<IncomingPayment> findByCustomerCode(String customerCode);
}
