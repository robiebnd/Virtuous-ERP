package com.digipals.wms.billing.entity;

import com.digipals.wms.common.entity.BaseDocument;
import com.digipals.wms.outbounddelivery.entity.OutboundDelivery;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billing_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BillingDocument extends BaseDocument {

    @Column(name = "billing_number", nullable = false, unique = true, length = 40)
    private String billingNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "outbound_delivery_id", nullable = false, unique = true)
    private OutboundDelivery outboundDelivery;

    @Column(name = "customer_code", nullable = false, length = 40)
    private String customerCode;

    @Column(name = "billing_type", nullable = false, length = 20)
    @Builder.Default
    private String billingType = "F2";

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BillingStatus status = BillingStatus.DRAFT;

    @Column(name = "billing_date", nullable = false)
    private LocalDateTime billingDate;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "billingDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BillingDocumentItem> items = new ArrayList<>();

    @PrePersist
    protected void prePersistBilling() {
        if (billingDate == null) billingDate = LocalDateTime.now();
        if (status == null) status = BillingStatus.DRAFT;
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;
        if (billingType == null || billingType.isBlank()) billingType = "F2";
    }

    public void addItem(BillingDocumentItem item) {
        items.add(item);
        item.setBillingDocument(this);
    }
}
