package com.digipals.wms.bintransfer.entity;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "bin_transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BinTransfer extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String transferNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_bin_id", nullable = false)
    private Bin fromBin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_bin_id", nullable = false)
    private Bin toBin;

    @Column(nullable = false)
    private LocalDateTime transferDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BinTransferStatus status = BinTransferStatus.DRAFT;

    @Column(length = 500)
    private String remarks;

    private LocalDateTime approvedAt;

    private LocalDateTime postedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by")
    private User postedBy;

    @PrePersist
    public void prePersist() {

        if (transferDate == null) {
            transferDate = LocalDateTime.now();
        }
    }
}