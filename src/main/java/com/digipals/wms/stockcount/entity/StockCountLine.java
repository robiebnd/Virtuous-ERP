package com.digipals.wms.stockcount.entity;

import com.digipals.wms.bin.entity.Bin;

import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "stock_count_lines")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCountLine {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "stock_count_id",
            nullable = false)
    private StockCount stockCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(
        name = "bin_id",
        nullable = false)
private Bin bin;

    @Column(nullable = false)
    private BigDecimal systemQuantity;

    private BigDecimal countedQuantity;

    @Column(name = "variance_quantity", nullable = true)
    private BigDecimal variance;

    private String reason;

// Helper method to automatically compute on demand if needed
public BigDecimal getCalculatedVariance() {
    if (countedQuantity == null || systemQuantity == null) {
        return BigDecimal.ZERO;
    }
    return countedQuantity.subtract(systemQuantity);
}

}