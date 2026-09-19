package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name="cost_center_budgets", uniqueConstraints=@UniqueConstraint(name="uq_cost_center_budget", columnNames={"cost_center_code","fiscal_year"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class CostCenterBudget extends BaseEntity {
    @Column(name="cost_center_code", nullable=false, length=30) private String costCenterCode;
    @Column(name="fiscal_year", nullable=false) private Integer fiscalYear;
    @Column(name="budget_amount", nullable=false, precision=19, scale=2) private BigDecimal budgetAmount;
    @Column(nullable=false, length=3) private String currency;
    @Column(length=250) private String description;
}
