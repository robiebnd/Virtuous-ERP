package com.digipals.wms.stockcount.entity;

import com.digipals.wms.stockadjustment.entity.StockAdjustment;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import com.digipals.wms.common.entity.BaseEntity;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_counts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCount extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String countNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StockCountStatus status =
            StockCountStatus.DRAFT;

    private String remarks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "count_date", nullable = false)
    private LocalDateTime countDate;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (countDate == null) {
            countDate = LocalDateTime.now();
        }

    }
    @OneToOne
    @JoinColumn(name = "stock_adjustment_id")
    private StockAdjustment stockAdjustment;

}