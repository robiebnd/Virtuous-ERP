package com.digipals.wms.payment.repository;

import com.digipals.wms.payment.entity.IncomingPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IncomingPaymentRepository extends JpaRepository<IncomingPayment, UUID> {
    List<IncomingPayment> findByCustomerCodeOrderByPaymentDateDesc(String customerCode);
}
