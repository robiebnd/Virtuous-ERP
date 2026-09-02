package com.digipals.wms.outbounddelivery.entity;

import com.digipals.wms.common.entity.BaseDocument;
import com.digipals.wms.salesorder.entity.SalesOrder;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "outbound_deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OutboundDelivery extends BaseDocument {

    @Column(name = "delivery_number", nullable = false, unique = true, length = 40)
    private String deliveryNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @Column(name = "customer_code", nullable = false, length = 40)
    private String customerCode;

    @Column(name = "shipping_point", nullable = false, length = 40)
    private String shippingPoint;

    @Column(name = "requested_delivery_date")
    private LocalDateTime requestedDeliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private OutboundDeliveryStatus status = OutboundDeliveryStatus.OPEN;

    @Column(name = "picked_at")
    private LocalDateTime pickedAt;

    @Column(name = "packed_at")
    private LocalDateTime packedAt;

    @Column(name = "goods_issue_at")
    private LocalDateTime goodsIssueAt;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutboundDeliveryItem> items = new ArrayList<>();

    @PrePersist
    protected void prePersistDelivery() {
        if (status == null) status = OutboundDeliveryStatus.OPEN;
    }

    public void addItem(OutboundDeliveryItem item) {
        items.add(item);
        item.setDelivery(this);
    }
}
