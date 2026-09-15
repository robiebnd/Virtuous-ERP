package com.digipals.wms.procurementclosure.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="supplier_payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SupplierPayment extends BaseEntity {
    @Column(name="payment_number",nullable=false,unique=true) private String paymentNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_id",nullable=false) private Supplier supplier;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_invoice_id",nullable=false) private SupplierInvoice invoice;
    @Column(name="payment_date",nullable=false) private LocalDateTime paymentDate;
    @Column(nullable=false,precision=18,scale=2) private BigDecimal amount;
    @Column(name="payment_method",nullable=false) private String paymentMethod;
    private String reference;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private SupplierPaymentStatus status;
}
