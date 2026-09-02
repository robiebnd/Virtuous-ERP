package com.digipals.wms.billing.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "billing_document_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BillingDocumentItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billing_document_id", nullable = false)
    private BillingDocument billingDocument;

    @Column(name = "item_number", nullable = false)
    private Integer itemNumber;

    @Column(name = "material_code", nullable = false, length = 40)
    private String materialCode;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "net_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal netValue;
}
