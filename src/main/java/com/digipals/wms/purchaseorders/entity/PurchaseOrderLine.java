package com.digipals.wms.purchaseorders.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionLine;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseOrderLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_requisition_line_id")
    private PurchaseRequisitionLine purchaseRequisitionLine;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(name = "received_quantity", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal receivedQuantity = BigDecimal.ZERO;

    @Column(name = "outstanding_quantity", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal outstandingQuantity = BigDecimal.ZERO;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false, precision = 18, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "order_unit", length = 20)
    private String orderUnit;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "storage_location", length = 40)
    private String storageLocation;

    @Column(name = "item_category", length = 30)
    private String itemCategory;

    @Column(name = "overdelivery_tolerance_percent", precision = 5, scale = 2)
    private BigDecimal overdeliveryTolerancePercent;

    @Column(name = "underdelivery_tolerance_percent", precision = 5, scale = 2)
    private BigDecimal underdeliveryTolerancePercent;

    @Column(name = "goods_receipt_required", nullable = false)
    private Boolean goodsReceiptRequired;

    @Column(name = "gr_based_invoice_verification", nullable = false)
    private Boolean grBasedInvoiceVerification;

    @Column(name = "confirmation_required", nullable = false)
    private Boolean confirmationRequired;

    @PrePersist
    @PreUpdate
    private void calculateValues() {
        if (quantity == null) quantity = BigDecimal.ZERO;
        if (receivedQuantity == null) receivedQuantity = BigDecimal.ZERO;
        outstandingQuantity = quantity.subtract(receivedQuantity);
        if (outstandingQuantity.compareTo(BigDecimal.ZERO) < 0) outstandingQuantity = BigDecimal.ZERO;
        lineTotal = unitPrice == null ? BigDecimal.ZERO : quantity.multiply(unitPrice);
        if (orderUnit == null || orderUnit.isBlank()) orderUnit = "EA";
        if (itemCategory == null || itemCategory.isBlank()) itemCategory = "STANDARD";
        if (overdeliveryTolerancePercent == null) overdeliveryTolerancePercent = BigDecimal.ZERO;
        if (underdeliveryTolerancePercent == null) underdeliveryTolerancePercent = BigDecimal.ZERO;
        if (goodsReceiptRequired == null) goodsReceiptRequired = Boolean.TRUE;
        if (grBasedInvoiceVerification == null) grBasedInvoiceVerification = Boolean.FALSE;
        if (confirmationRequired == null) confirmationRequired = Boolean.FALSE;
    }
}
