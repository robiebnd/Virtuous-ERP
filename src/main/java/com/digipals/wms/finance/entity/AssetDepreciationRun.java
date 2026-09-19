package com.digipals.wms.finance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="asset_depreciation_runs", uniqueConstraints=@UniqueConstraint(name="uq_asset_depreciation_period", columnNames={"asset_id","period_start"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetDepreciationRun {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="asset_id",nullable=false) private FixedAsset asset;
    @Column(name="period_start",nullable=false) private LocalDate periodStart;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
    @Column(name="accounting_document_id",nullable=false) private UUID accountingDocumentId;
}