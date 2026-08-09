package com.digipals.wms.goodsmovement.entity;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "goods_movement_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GoodsMovementLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "goods_movement_id",
            nullable = false
    )
    private GoodsMovement goodsMovement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_bin_id")
    private Bin fromBin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_bin_id")
    private Bin toBin;

    @Column(
            nullable = false,
            precision = 18,
            scale = 2
    )
    private BigDecimal quantity;

    @Column(
            name = "unit_cost",
            precision = 18,
            scale = 2
    )
    private BigDecimal unitCost;

    @Column(length = 500)
    private String remarks;
}
