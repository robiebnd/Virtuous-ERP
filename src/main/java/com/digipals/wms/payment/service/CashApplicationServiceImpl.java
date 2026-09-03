package com.digipals.wms.payment.service;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingStatus;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.payment.dto.CashApplicationRequest;
import com.digipals.wms.payment.entity.IncomingPayment;
import com.digipals.wms.payment.entity.PaymentAllocation;
import com.digipals.wms.payment.entity.PaymentStatus;
import com.digipals.wms.payment.repository.IncomingPaymentRepository;
import com.digipals.wms.payment.repository.PaymentAllocationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CashApplicationServiceImpl implements CashApplicationService {

    private final IncomingPaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final BillingDocumentRepository billingDocumentRepository;

    @Override
    public IncomingPayment apply(CashApplicationRequest request) {
        IncomingPayment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new EntityNotFoundException("Incoming payment not found: " + request.paymentId()));

        BillingDocument billing = billingDocumentRepository.findById(request.billingDocumentId())
                .orElseThrow(() -> new EntityNotFoundException("Billing document not found: " + request.billingDocumentId()));

        if (billing.getStatus() != BillingStatus.POSTED) {
            throw new IllegalStateException("Cash can only be applied to a posted billing document");
        }
        if (!payment.getCustomerCode().equalsIgnoreCase(billing.getCustomerCode())) {
            throw new IllegalArgumentException("Payment and billing document belong to different customers");
        }
        if (!payment.getCurrency().equalsIgnoreCase(billing.getCurrency())) {
            throw new IllegalArgumentException("Payment currency must match billing document currency");
        }
        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled payment cannot be applied");
        }

        BigDecimal alreadyApplied = payment.getAllocations().stream()
                .map(PaymentAllocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal unapplied = payment.getAmount().subtract(alreadyApplied);
        if (unapplied.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Payment has no unapplied balance");
        }

        BigDecimal billingApplied = allocationRepository.findByBillingDocumentId(billing.getId()).stream()
                .map(PaymentAllocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal billingOutstanding = billing.getTotalAmount().subtract(billingApplied);
        if (billingOutstanding.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Billing document is already fully paid");
        }

        BigDecimal applied = request.amount().min(unapplied).min(billingOutstanding);
        if (applied.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Application amount must be greater than zero");
        }

        PaymentAllocation allocation = PaymentAllocation.builder()
                .billingDocument(billing)
                .amount(applied)
                .build();
        payment.addAllocation(allocation);

        BigDecimal newUnapplied = unapplied.subtract(applied);
        payment.setStatus(newUnapplied.compareTo(BigDecimal.ZERO) == 0
                ? PaymentStatus.FULLY_APPLIED
                : PaymentStatus.PARTIALLY_APPLIED);

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public IncomingPayment findPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Incoming payment not found: " + paymentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomingPayment> findCustomerPayments(String customerCode) {
        return paymentRepository.findByCustomerCodeOrderByPaymentDateDesc(customerCode);
    }
}
