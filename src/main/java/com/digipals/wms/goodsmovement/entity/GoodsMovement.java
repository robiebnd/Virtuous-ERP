package com.digipals.wms.goodsmovement.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "goods_movements"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GoodsMovement extends BaseEntity {

    @Column(
            name = "movement_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String movementNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "movement_type",
            nullable = false,
            length = 50
    )
    private GoodsMovementType movementType;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private GoodsMovementStatus status =
            GoodsMovementStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "warehouse_id",
            nullable = false
    )
    private Warehouse warehouse;

    @Column(
            name = "reference_number",
            nullable = false,
            length = 50
    )
    private String referenceNumber;

    @Column(
            name = "reference_type",
            nullable = false,
            length = 50
    )
    private String referenceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @Column(
            name = "movement_date",
            nullable = false
    )
    @Builder.Default
    private LocalDateTime movementDate =
            LocalDateTime.now();

    @Column(length = 1000)
    private String remarks;
}
