package com.digipals.wms.outbounddelivery.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "outbound_delivery_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OutboundDeliveryItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_id", nullable = false)
    private OutboundDelivery delivery;

    @Column(name = "item_number", nullable = false)
    private Integer itemNumber;

    @Column(name = "material_code", nullable = false, length = 40)
    private String materialCode;

    @Column(name = "ordered_quantity", nullable = false, precision = 19, scale = 3)
    private BigDecimal orderedQuantity;

    @Column(name = "delivered_quantity", nullable = false, precision = 19, scale = 3)
    @Builder.Default
    private BigDecimal deliveredQuantity = BigDecimal.ZERO;

    @Column(name = "picked_quantity", nullable = false, precision = 19, scale = 3)
    @Builder.Default
    private BigDecimal pickedQuantity = BigDecimal.ZERO;

    @Column(name = "packed_quantity", nullable = false, precision = 19, scale = 3)
    @Builder.Default
    private BigDecimal packedQuantity = BigDecimal.ZERO;
}
