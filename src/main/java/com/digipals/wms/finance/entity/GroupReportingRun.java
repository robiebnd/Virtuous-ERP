package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="group_reporting_runs", uniqueConstraints=@UniqueConstraint(name="uq_group_run",columnNames={"group_id","fiscal_year","period_number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class GroupReportingRun extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="group_id",nullable=false) private ConsolidationGroup group;
    @Column(name="fiscal_year",nullable=false) private Integer fiscalYear;
    @Column(name="period_number",nullable=false) private Integer periodNumber;
    @Column(name="reporting_currency",nullable=false,length=3) private String reportingCurrency;
    @Column(nullable=false,length=20) private String status;
    @Column(name="total_debit",nullable=false,precision=19,scale=2) private BigDecimal totalDebit;
    @Column(name="total_credit",nullable=false,precision=19,scale=2) private BigDecimal totalCredit;
    @Column(name="translation_adjustment",nullable=false,precision=19,scale=2) private BigDecimal translationAdjustment;
    @Column(name="nci_amount",nullable=false,precision=19,scale=2) private BigDecimal nciAmount;
    @Column(name="completed_at") private LocalDateTime completedAt;
}
