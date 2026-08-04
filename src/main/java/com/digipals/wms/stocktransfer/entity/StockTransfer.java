package com.digipals.wms.stocktransfer.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockTransfer extends BaseEntity {

    @Column(name = "transfer_number",
            nullable = false,
            unique = true)
    private String transferNumber;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "from_warehouse_id", nullable = false)
   private Warehouse sourceWarehouse;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "to_warehouse_id", nullable = false)
   private Warehouse destinationWarehouse;

    @Column(name = "transfer_date", nullable = false)
    private LocalDateTime transferDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockTransferStatus status;

    @Column(length = 3000)
    private String remarks;

    @Column(name = "transferred_at")
    private LocalDateTime transferredAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private User issuedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "created_by")
private User createdBy;

    @PrePersist
    protected void prePersist() {

        if (status == null) {
            status = StockTransferStatus.DRAFT;
        }

        if (transferredAt == null) {
            transferredAt = LocalDateTime.now();
        }
    }
}