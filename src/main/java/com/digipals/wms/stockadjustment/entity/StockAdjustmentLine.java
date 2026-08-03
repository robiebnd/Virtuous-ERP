package com.digipals.wms.stockadjustment.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.bin.entity.Bin;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
@Entity
@Table(name = "stock_adjustment_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockAdjustmentLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_adjustment_id", nullable = false)
    private StockAdjustment stockAdjustment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    @Column(name = "system_quantity",
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal systemQuantity;

    @Column(name = "counted_quantity",
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal countedQuantity;

    @Column(nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal difference;

   /*  @Column(name = "quantity_before", nullable = false)
    private BigDecimal quantityBefore;*/

    @Column(name = "adjustment_quantity", nullable = false)
    private BigDecimal adjustmentQuantity;


    @Column(length = 500)
    private String reason;

    @PrePersist
    @PreUpdate
    private void calculateDifference() {

     if (systemQuantity != null &&
        countedQuantity != null) {

        this.difference =
                countedQuantity.subtract(
                        systemQuantity);
    }
  }
}