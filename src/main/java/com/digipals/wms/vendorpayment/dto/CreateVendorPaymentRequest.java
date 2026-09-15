package com.digipals.wms.vendorpayment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateVendorPaymentRequest {
    @NotNull private UUID vendorInvoiceId;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    private String currency;
    private String reference;
    private String remarks;
}
