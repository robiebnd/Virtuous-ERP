package com.digipals.wms.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CashApplicationResponse(
        UUID allocationId,
        UUID paymentId,
        String paymentNumber,
        UUID billingDocumentId,
        String billingNumber,
        BigDecimal amount,
        BigDecimal paymentAppliedTotal,
        BigDecimal paymentUnappliedAmount,
        BigDecimal invoiceAppliedTotal,
        BigDecimal invoiceOutstandingAmount
) {
}
