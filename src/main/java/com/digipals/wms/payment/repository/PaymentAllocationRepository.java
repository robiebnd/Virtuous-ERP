package com.digipals.wms.payment.repository;

import com.digipals.wms.payment.entity.PaymentAllocation;
import com.digipals.wms.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, UUID> {

    List<PaymentAllocation> findByBillingDocumentId(UUID billingDocumentId);

    @Query("select pa from PaymentAllocation pa " +
           "where pa.billingDocument.id = :billingDocumentId " +
           "and pa.payment.status <> :status")
    List<PaymentAllocation> findActiveByBillingDocumentId(
            @Param("billingDocumentId") UUID billingDocumentId,
            @Param("status") PaymentStatus status);

    @Query("select pa from PaymentAllocation pa join fetch pa.billingDocument " +
           "where pa.payment.id = :paymentId " +
           "and pa.payment.status <> :status " +
           "order by pa.createdAt asc")
    List<PaymentAllocation> findActiveByPaymentId(
            @Param("paymentId") UUID paymentId,
            @Param("status") PaymentStatus status);

    @Query("select coalesce(sum(pa.amount), 0) from PaymentAllocation pa " +
           "where pa.payment.id = :paymentId " +
           "and pa.payment.status <> :status")
    BigDecimal sumActiveAmountByPaymentId(
            @Param("paymentId") UUID paymentId,
            @Param("status") PaymentStatus status);
}
