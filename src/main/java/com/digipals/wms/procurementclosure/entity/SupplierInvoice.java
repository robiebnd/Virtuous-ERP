package com.digipals.wms.procurementclosure.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="supplier_invoices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SupplierInvoice extends BaseEntity {
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
    @OneToMany(mappedBy="invoice",fetch=FetchType.LAZY,cascade=CascadeType.ALL)
    @Builder.Default private List<SupplierInvoiceLine> lines = new ArrayList<>();
}
