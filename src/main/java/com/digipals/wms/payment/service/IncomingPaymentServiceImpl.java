package com.digipals.wms.payment.service;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.billing.entity.BillingStatus;
import com.digipals.wms.billing.repository.BillingDocumentRepository;
import com.digipals.wms.payment.dto.CreateIncomingPaymentRequest;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IncomingPaymentServiceImpl implements IncomingPaymentService {

    private final IncomingPaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final BillingDocumentRepository billingDocumentRepository;

    @Override
    public IncomingPayment receive(CreateIncomingPaymentRequest request) {
        BillingDocument billing = billingDocumentRepository.findById(request.billingDocumentId())
                .orElseThrow(() -> new EntityNotFoundException("Billing document not found: " + request.billingDocumentId()));

        if (billing.getStatus() != BillingStatus.POSTED) {
            throw new IllegalStateException("Payment can only be received against a posted billing document");
        }

        String paymentCurrency = request.currency().trim().toUpperCase(Locale.ROOT);
        if (!billing.getCurrency().equalsIgnoreCase(paymentCurrency)) {
            throw new IllegalArgumentException("Payment currency must match billing document currency");
        }

        BigDecimal previousApplied = allocationRepository.findByBillingDocumentId(billing.getId()).stream()
                .map(PaymentAllocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal outstanding = billing.getTotalAmount().subtract(previousApplied);
        if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Billing document is already fully paid");
        }

        BigDecimal appliedAmount = request.amount().min(outstanding);

        IncomingPayment payment = IncomingPayment.builder()
                .paymentNumber(nextPaymentNumber())
                .customerCode(billing.getCustomerCode())
                .amount(request.amount())
                .currency(paymentCurrency)
                .paymentDate(LocalDateTime.now())
                .reference(request.reference())
                .status(appliedAmount.compareTo(BigDecimal.ZERO) == 0
                        ? PaymentStatus.RECEIVED
                        : appliedAmount.compareTo(request.amount()) == 0
                            ? PaymentStatus.FULLY_APPLIED
                            : PaymentStatus.PARTIALLY_APPLIED)
                .build();

        PaymentAllocation allocation = PaymentAllocation.builder()
                .billingDocument(billing)
                .amount(appliedAmount)
                .build();
        payment.addAllocation(allocation);

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public IncomingPayment findById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incoming payment not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomingPayment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomingPayment> findByCustomerCode(String customerCode) {
        return paymentRepository.findByCustomerCodeOrderByPaymentDateDesc(customerCode);
    }

    private String nextPaymentNumber() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
