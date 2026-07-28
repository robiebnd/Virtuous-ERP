package com.digipals.wms.stockadjustment.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_adjustments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockAdjustment extends BaseEntity {

    @Column(name = "adjustment_number", nullable = false, unique = true)
    private String adjustmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentStatus status;

    @Column(length = 255)
    private String reason;

    @Column(length = 3000)
    private String remarks;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "adjustment_date", nullable = false)
    private LocalDateTime adjustmentDate;

    @PrePersist
    protected void prePersist() {

        if (adjustmentDate == null) {
        adjustmentDate = LocalDateTime.now();
    }
        if (status == null) {
            status = AdjustmentStatus.DRAFT;
        }
    }
}