package com.digipals.wms.payment.entity;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.common.entity.BaseDocument;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incoming_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IncomingPayment extends BaseDocument {

    @Column(name = "payment_number", nullable = false, unique = true, length = 40)
    private String paymentNumber;

    @Column(name = "customer_code", nullable = false, length = 40)
    private String customerCode;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "reference", length = 100)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.RECEIVED;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PaymentAllocation> allocations = new ArrayList<>();

    @PrePersist
    protected void prePersistPayment() {
        if (paymentDate == null) paymentDate = LocalDateTime.now();
        if (status == null) status = PaymentStatus.RECEIVED;
    }

    public void addAllocation(PaymentAllocation allocation) {
        allocations.add(allocation);
        allocation.setPayment(this);
    }
}
