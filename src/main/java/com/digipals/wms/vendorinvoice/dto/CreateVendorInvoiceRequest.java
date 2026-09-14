package com.digipals.wms.vendorinvoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CreateVendorInvoiceRequest {
    @NotNull private UUID purchaseOrderId;
    @NotNull private UUID goodsReceiptId;
    @NotBlank private String supplierInvoiceNumber;
    private LocalDateTime invoiceDate;
    @NotBlank @Size(min=3,max=3) private String currency;
    @NotNull @DecimalMin("0.00") private BigDecimal taxAmount = BigDecimal.ZERO;
    @NotEmpty @Valid private List<Line> lines;

    @Data
    public static class Line {
        @NotNull private UUID purchaseOrderLineId;
        private UUID goodsReceiptLineId;
        @NotNull @DecimalMin("0.01") private BigDecimal invoicedQuantity;
        @NotNull @DecimalMin("0.00") private BigDecimal invoiceUnitPrice;
    }
}
