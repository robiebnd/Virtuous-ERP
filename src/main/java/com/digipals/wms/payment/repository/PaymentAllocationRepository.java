package com.digipals.wms.payment.repository;

import com.digipals.wms.payment.entity.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, UUID> {
    List<PaymentAllocation> findByBillingDocumentId(UUID billingDocumentId);
}
