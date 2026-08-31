package com.digipals.wms.goodsreceiving.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "goods_receipt_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GoodsReceiptLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_receipt_id", nullable = false)
    private GoodsReceipt goodsReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_line_id", nullable = false)
    private PurchaseOrderLine purchaseOrderLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "ordered_quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal orderedQuantity;

    /** Quantity already received before this GRN was created. This is historical and must not be overwritten on approval. */
    @Column(name = "previously_received_quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal previouslyReceivedQuantity;

    @Column(name = "received_quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal receivedQuantity;

    @Column(name = "accepted_quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal acceptedQuantity;

    @Column(name = "rejected_quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal rejectedQuantity;

    @Column(name = "unit_cost", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitCost;

    @Column(length = 500)
    private String remarks;

    @PrePersist
    private void initializePreviouslyReceivedQuantity() {
        if (previouslyReceivedQuantity == null) {
            previouslyReceivedQuantity = purchaseOrderLine == null || purchaseOrderLine.getReceivedQuantity() == null
                    ? BigDecimal.ZERO
                    : purchaseOrderLine.getReceivedQuantity();
        }
    }
}
