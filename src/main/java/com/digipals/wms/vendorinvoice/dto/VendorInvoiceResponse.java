package com.digipals.wms.vendorinvoice.dto;

import com.digipals.wms.vendorinvoice.entity.VendorInvoiceMatchStatus;
import com.digipals.wms.vendorinvoice.entity.VendorInvoiceStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder
public class VendorInvoiceResponse {
    private UUID id;
    private String invoiceNumber;
    private String supplierInvoiceNumber;
    private UUID supplierId;
    private String supplierCode;
    private String supplierName;
    private UUID purchaseOrderId;
    private String purchaseOrderNumber;
    private UUID goodsReceiptId;
    private String goodsReceiptNumber;
    private LocalDateTime invoiceDate;
    private String currency;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private VendorInvoiceStatus status;
    private VendorInvoiceMatchStatus matchStatus;
    private String blockReason;
    private List<Line> lines;

    @Data @Builder
    public static class Line {
        private UUID id;
        private UUID purchaseOrderLineId;
        private UUID goodsReceiptLineId;
        private UUID productId;
        private String sku;
        private BigDecimal invoicedQuantity;
        private BigDecimal invoiceUnitPrice;
        private BigDecimal lineTotal;
        private BigDecimal quantityVariance;
        private BigDecimal priceVariance;
        private VendorInvoiceMatchStatus matchStatus;
        private String blockReason;
    }
}
