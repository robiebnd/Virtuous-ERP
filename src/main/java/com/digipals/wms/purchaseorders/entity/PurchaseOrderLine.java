package com.digipals.wms.purchaseorders.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseOrderLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "purchase_order_id",
            nullable = false)
    private PurchaseOrder purchaseOrder;

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
            name = "received_quantity",
            nullable = false,
            precision = 18,
            scale = 2)
    @Builder.Default
    private BigDecimal receivedQuantity = BigDecimal.ZERO;

    @Column(
            name = "outstanding_quantity",
            nullable = false,
            precision = 18,
            scale = 2)
    @Builder.Default
    private BigDecimal outstandingQuantity = BigDecimal.ZERO;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal unitPrice;

    @Column(
            name = "line_total",
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal lineTotal;

    @PrePersist
    @PreUpdate
    private void calculateValues() {

        if (quantity == null) {
            quantity = BigDecimal.ZERO;
        }

        if (receivedQuantity == null) {
            receivedQuantity = BigDecimal.ZERO;
        }

        outstandingQuantity =
                quantity.subtract(receivedQuantity);

        if (outstandingQuantity.compareTo(BigDecimal.ZERO) < 0) {
            outstandingQuantity = BigDecimal.ZERO;
        }

        if (unitPrice != null) {
            lineTotal = quantity.multiply(unitPrice);
        } else {
            lineTotal = BigDecimal.ZERO;
        }
    }
}