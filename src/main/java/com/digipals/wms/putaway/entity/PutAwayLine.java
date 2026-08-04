package com.digipals.wms.putaway.entity;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.goodsreceiving.entity.GoodsReceiptLine;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Entity
@Table(name = "put_away_lines")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PutAwayLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "put_away_id", nullable = false)
    private PutAway putAway;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_receipt_line_id", nullable = false)
    private GoodsReceiptLine goodsReceiptLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_bin_id", nullable = false)
    private Bin fromBin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_bin_id")
    private Bin toBin;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal plannedQuantity;

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal completedQuantity = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private PutAwayLineStatus status;
}