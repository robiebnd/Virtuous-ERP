package com.digipals.wms.procurementclosure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class ProcurementClosureRequests {
    private ProcurementClosureRequests() {}

    public record SupplierInvoiceRequest(
            String purchaseOrderNumber,
            String supplierInvoiceNumber,
            LocalDateTime invoiceDate,
            String currency,
            List<InvoiceLineRequest> lines) {}

    public record InvoiceLineRequest(
            String sku,
            BigDecimal quantity,
            BigDecimal unitPrice) {}

    public record SupplierPaymentRequest(
            String invoiceNumber,
            BigDecimal amount,
            String paymentMethod,
            String reference) {}

    public record VendorEvaluationRequest(
            String purchaseOrderNumber,
            BigDecimal priceScore,
            BigDecimal qualityScore,
            BigDecimal deliveryScore,
            BigDecimal serviceScore,
            String remarks) {}

    public record GoodsIssueRequest(
            String warehouseCode,
            String sku,
            String binCode,
            BigDecimal quantity,
            String referenceNumber,
            String referenceType,
            String remarks) {}
}
