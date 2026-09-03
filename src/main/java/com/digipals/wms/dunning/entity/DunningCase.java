package com.digipals.wms.dunning.entity;

import com.digipals.wms.billing.entity.BillingDocument;
import com.digipals.wms.common.entity.BaseDocument;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dunning_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DunningCase extends BaseDocument {

    @Column(name = "dunning_number", nullable = false, unique = true, length = 40)
    private String dunningNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billing_document_id", nullable = false)
    private BillingDocument billingDocument;

    @Column(name = "customer_code", nullable = false, length = 40)
    private String customerCode;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "outstanding_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal outstandingAmount;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "dunning_date", nullable = false)
    private LocalDateTime dunningDate;

    @Column(name = "dunning_level", nullable = false)
    private Integer dunningLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DunningStatus status = DunningStatus.OPEN;

    @Column(name = "message", length = 3000)
    private String message;

    @PrePersist
    protected void prePersistDunning() {
        if (dunningDate == null) dunningDate = LocalDateTime.now();
        if (status == null) status = DunningStatus.OPEN;
        if (dunningLevel == null) dunningLevel = 1;
    }
}
