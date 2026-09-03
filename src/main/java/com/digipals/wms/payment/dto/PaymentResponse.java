package com.digipals.wms.payment.dto;

import com.digipals.wms.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        String paymentNumber,
        String customerCode,
        BigDecimal amount,
        BigDecimal appliedAmount,
        BigDecimal unappliedAmount,
        String currency,
        LocalDateTime paymentDate,
        String reference,
        PaymentStatus status,
        UUID billingDocumentId
) {
}
