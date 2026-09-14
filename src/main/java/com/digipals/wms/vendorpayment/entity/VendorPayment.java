package com.digipals.wms.vendorpayment.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.vendorinvoice.entity.VendorInvoice;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="vendor_payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class VendorPayment extends BaseEntity {
    @Column(name="payment_number", nullable=false, unique=true, length=60) private String paymentNumber;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="supplier_id", nullable=false) private Supplier supplier;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="vendor_invoice_id", nullable=false) private VendorInvoice vendorInvoice;
    @Column(nullable=false, precision=19, scale=2) private BigDecimal amount;
    @Column(nullable=false, length=3) private String currency;
    @Column(name="payment_date", nullable=false) private LocalDateTime paymentDate;
    @Column(name="reference", length=100) private String reference;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) @Builder.Default private VendorPaymentStatus status=VendorPaymentStatus.DRAFT;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="processed_by") private User processedBy;
    @Column(length=1000) private String remarks;
    @PrePersist protected void prePersistPayment() {
        if(paymentDate==null) paymentDate=LocalDateTime.now();
        if(currency!=null) currency=currency.trim().toUpperCase();
        if(status==null) status=VendorPaymentStatus.DRAFT;
    }
}
