package com.digipals.wms.vendorpayment.dto;

import com.digipals.wms.vendorpayment.entity.VendorPaymentStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VendorPaymentResponse {
    private UUID id;
    private String paymentNumber;
    private UUID vendorInvoiceId;
    private String invoiceNumber;
    private UUID supplierId;
    private String supplierCode;
    private String supplierName;
    private LocalDateTime paymentDate;
    private String currency;
    private BigDecimal amount;
    private String paymentMethod;
    private String referenceNumber;
    private VendorPaymentStatus status;
    private String remarks;
}
