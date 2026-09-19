package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="internal_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class InternalOrder extends BaseEntity {
    @Column(name="order_code", nullable=false, unique=true, length=40) private String orderCode;
    @Column(nullable=false, length=150) private String name;
    @Column(length=500) private String description;
    @Column(name="cost_center_code", length=30) private String costCenterCode;
    @Column(name="responsible_person", length=150) private String responsiblePerson;
    @Column(name="budget_amount", nullable=false, precision=19, scale=2) private BigDecimal budgetAmount;
    @Column(nullable=false, length=3) private String currency;
    @Column(nullable=false, length=20) private String status;
    @Column(name="start_date") private LocalDate startDate;
    @Column(name="end_date") private LocalDate endDate;
}
