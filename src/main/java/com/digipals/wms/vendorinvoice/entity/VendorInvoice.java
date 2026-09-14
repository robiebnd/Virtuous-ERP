package com.digipals.wms.vendorinvoice.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.goodsreceiving.entity.GoodsReceipt;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendor_invoices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class VendorInvoice extends BaseEntity {
    @Column(name="invoice_number", nullable=false, unique=true, length=60)
    private String invoiceNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_id", nullable=false)
    private Supplier supplier;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="purchase_order_id", nullable=false)
    private PurchaseOrder purchaseOrder;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="goods_receipt_id", nullable=false)
    private GoodsReceipt goodsReceipt;
    @Column(name="supplier_invoice_number", nullable=false, length=100)
    private String supplierInvoiceNumber;
    @Column(name="invoice_date", nullable=false) private LocalDateTime invoiceDate;
    @Column(nullable=false, length=3) private String currency;
    @Column(nullable=false, precision=19, scale=2) @Builder.Default private BigDecimal subtotal=BigDecimal.ZERO;
    @Column(name="tax_amount", nullable=false, precision=19, scale=2) @Builder.Default private BigDecimal taxAmount=BigDecimal.ZERO;
    @Column(name="total_amount", nullable=false, precision=19, scale=2) @Builder.Default private BigDecimal totalAmount=BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) @Builder.Default private VendorInvoiceStatus status=VendorInvoiceStatus.DRAFT;
    @Enumerated(EnumType.STRING) @Column(name="match_status", nullable=false, length=20) @Builder.Default private VendorInvoiceMatchStatus matchStatus=VendorInvoiceMatchStatus.PENDING;
    @Column(name="block_reason", length=1000) private String blockReason;
    @Column(name="posted_at") private LocalDateTime postedAt;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private User createdBy;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="approved_by") private User approvedBy;
    @Column(name="approved_at") private LocalDateTime approvedAt;
    @OneToMany(mappedBy="vendorInvoice", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default private List<VendorInvoiceLine> lines=new ArrayList<>();

    @PrePersist
    protected void prePersistInvoice() {
        if(invoiceDate==null) invoiceDate=LocalDateTime.now();
        if(currency!=null) currency=currency.trim().toUpperCase();
        if(subtotal==null) subtotal=BigDecimal.ZERO;
        if(taxAmount==null) taxAmount=BigDecimal.ZERO;
        if(totalAmount==null) totalAmount=subtotal.add(taxAmount);
        if(status==null) status=VendorInvoiceStatus.DRAFT;
        if(matchStatus==null) matchStatus=VendorInvoiceMatchStatus.PENDING;
    }

    public void addLine(VendorInvoiceLine line) { lines.add(line); line.setVendorInvoice(this); }
}
