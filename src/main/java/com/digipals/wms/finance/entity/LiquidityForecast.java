package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="liquidity_forecasts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class LiquidityForecast extends BaseEntity {
    @Column(name="forecast_date",nullable=false) private LocalDate forecastDate;
    @Column(nullable=false,length=80) private String category;
    @Column(length=250) private String description;
    @Column(name="expected_inflow",nullable=false,precision=19,scale=2) private BigDecimal expectedInflow;
    @Column(name="expected_outflow",nullable=false,precision=19,scale=2) private BigDecimal expectedOutflow;
    @Column(nullable=false,length=3) private String currency;
    @Column(nullable=false,length=20) private String status;
}