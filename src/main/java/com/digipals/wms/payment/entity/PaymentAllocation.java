package com.digipals.wms.payment.entity;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_allocations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentAllocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private IncomingPayment payment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billing_document_id", nullable = false)
    private BillingDocument billingDocument;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
}
