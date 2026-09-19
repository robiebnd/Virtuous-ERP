package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "fiscal_periods", uniqueConstraints = @UniqueConstraint(name = "uq_fiscal_period", columnNames = {"company_code","fiscal_year","period_number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class FiscalPeriod extends BaseEntity {
    @Column(name="company_code", nullable=false, length=20)
    private String companyCode;
    @Column(name="fiscal_year", nullable=false)
    private Integer fiscalYear;
    @Column(name="period_number", nullable=false)
    private Integer periodNumber;
    @Column(name="period_name", nullable=false, length=40)
    private String periodName;
    @Column(name="start_date", nullable=false)
    private LocalDate startDate;
    @Column(name="end_date", nullable=false)
    private LocalDate endDate;
    @Column(nullable=false, length=20)
    private String status;
    @Column(name="closed_at")
    private java.time.LocalDateTime closedAt;
    @Column(name="closed_by", length=120)
    private String closedBy;
}