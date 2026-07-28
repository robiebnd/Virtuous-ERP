package com.digipals.wms.goodsreceiving.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "goods_receipts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GoodsReceipt extends BaseEntity {

    @Column(
            name = "grn_number",
            nullable = false,
            unique = true)
    private String grnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "purchase_order_id",
            nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "warehouse_id",
            nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptStatus status;

    @Column(name = "supplier_delivery_note")
    private String supplierDeliveryNote;

    @Column(length = 1000)
    private String remarks;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}