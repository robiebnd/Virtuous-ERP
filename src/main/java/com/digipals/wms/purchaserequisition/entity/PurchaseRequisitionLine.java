package com.digipals.wms.purchaserequisition.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_requisition_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseRequisitionLine
        extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "purchase_requisition_id",
            nullable = false)
    private PurchaseRequisition purchaseRequisition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false)
    private Product product;

    @Column(
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal quantity;

    @Column(
            name = "estimated_unit_cost",
            precision = 18,
            scale = 2)
    private BigDecimal estimatedUnitCost;

    @Column(length = 500)
    private String remarks;
}