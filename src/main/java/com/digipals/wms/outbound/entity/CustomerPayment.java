package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="customer_payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class CustomerPayment extends BaseEntity {
    @Column(name="payment_number", nullable=false, unique=true, length=50) private String paymentNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id", nullable=false) private Customer customer;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="invoice_id", nullable=false) private CustomerInvoice invoice;
    @Column(nullable=false) private LocalDateTime paymentDate;
    @Column(nullable=false) private BigDecimal amount;
    @Column(nullable=false) private String paymentMethod;
    private String reference;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private PaymentStatus status;
}
