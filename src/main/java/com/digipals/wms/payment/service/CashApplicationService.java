package com.digipals.wms.payment.service;

import com.digipals.wms.payment.dto.CashApplicationRequest;
import com.digipals.wms.payment.entity.IncomingPayment;

import java.util.List;
import java.util.UUID;

public interface CashApplicationService {
    IncomingPayment apply(CashApplicationRequest request);
    IncomingPayment findPayment(UUID paymentId);
    List<IncomingPayment> findCustomerPayments(String customerCode);
}
