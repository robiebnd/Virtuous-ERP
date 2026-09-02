package com.digipals.wms.procurementclosure.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ProcurementClosureEntities { private ProcurementClosureEntities() {} }

@Entity @Table(name="supplier_invoices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class SupplierInvoice extends BaseEntity {
    @Column(name="invoice_number",nullable=false,unique=true) private String invoiceNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_id",nullable=false) private Supplier supplier;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="purchase_order_id",nullable=false) private PurchaseOrder purchaseOrder;
    @Column(name="invoice_date",nullable=false) private LocalDateTime invoiceDate;
    private String currency;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private SupplierInvoiceStatus status;
    @Column(nullable=false,precision=18,scale=2) private BigDecimal subtotal;
    @Column(name="total_amount",nullable=false,precision=18,scale=2) private BigDecimal totalAmount;
    @Column(name="amount_paid",nullable=false,precision=18,scale=2) private BigDecimal amountPaid;
    @Column(name="balance_due",nullable=false,precision=18,scale=2) private BigDecimal balanceDue;
    @Column(name="blocked_reason",length=500) private String blockedReason;
}

enum SupplierInvoiceStatus { POSTED, BLOCKED, PARTIALLY_PAID, PAID }

@Entity @Table(name="supplier_invoice_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class SupplierInvoiceLine extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_invoice_id",nullable=false) private SupplierInvoice invoice;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="purchase_order_line_id",nullable=false) private PurchaseOrderLine purchaseOrderLine;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) private Product product;
    @Column(nullable=false,precision=18,scale=2) private BigDecimal quantity;
    @Column(name="unit_price",nullable=false,precision=18,scale=2) private BigDecimal unitPrice;
    @Column(name="line_total",nullable=false,precision=18,scale=2) private BigDecimal lineTotal;
}

@Entity @Table(name="supplier_payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class SupplierPayment extends BaseEntity {
    @Column(name="payment_number",nullable=false,unique=true) private String paymentNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_id",nullable=false) private Supplier supplier;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_invoice_id",nullable=false) private SupplierInvoice invoice;
    @Column(name="payment_date",nullable=false) private LocalDateTime paymentDate;
    @Column(nullable=false,precision=18,scale=2) private BigDecimal amount;
    @Column(name="payment_method",nullable=false) private String paymentMethod;
    private String reference;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private SupplierPaymentStatus status;
}

enum SupplierPaymentStatus { CLEARED }

@Entity @Table(name="vendor_evaluations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
class VendorEvaluation extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_id",nullable=false) private Supplier supplier;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="purchase_order_id") private PurchaseOrder purchaseOrder;
    @Column(name="price_score",nullable=false,precision=5,scale=2) private BigDecimal priceScore;
    @Column(name="quality_score",nullable=false,precision=5,scale=2) private BigDecimal qualityScore;
    @Column(name="delivery_score",nullable=false,precision=5,scale=2) private BigDecimal deliveryScore;
    @Column(name="service_score",nullable=false,precision=5,scale=2) private BigDecimal serviceScore;
    @Column(name="overall_score",nullable=false,precision=5,scale=2) private BigDecimal overallScore;
    @Column(name="evaluation_date",nullable=false) private LocalDateTime evaluationDate;
    @Column(length=1000) private String remarks;
}
