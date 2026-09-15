package com.digipals.wms.vendorinvoice.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.products.Product;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name="vendor_invoice_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class VendorInvoiceLine extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="vendor_invoice_id", nullable=false)
    private VendorInvoice vendorInvoice;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="purchase_order_line_id", nullable=false)
    private PurchaseOrderLine purchaseOrderLine;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="goods_receipt_line_id")
    private GoodsReceiptLine goodsReceiptLine;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false)
    private Product product;
    @Column(name="invoiced_quantity", nullable=false, precision=18, scale=2) private BigDecimal invoicedQuantity;
    @Column(name="invoice_unit_price", nullable=false, precision=18, scale=2) private BigDecimal invoiceUnitPrice;
    @Column(name="line_total", nullable=false, precision=19, scale=2) private BigDecimal lineTotal;
    @Column(name="quantity_variance", nullable=false, precision=18, scale=2) @Builder.Default private BigDecimal quantityVariance=BigDecimal.ZERO;
    @Column(name="price_variance", nullable=false, precision=18, scale=2) @Builder.Default private BigDecimal priceVariance=BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) @Column(name="match_status", nullable=false, length=20) @Builder.Default private VendorInvoiceMatchStatus matchStatus=VendorInvoiceMatchStatus.PENDING;
    @Column(name="block_reason", length=1000) private String blockReason;
}
