package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="fixed_assets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class FixedAsset extends BaseEntity {
    @Column(name="asset_number",nullable=false,unique=true,length=40) private String assetNumber;
    @Column(name="asset_name",nullable=false,length=200) private String assetName;
    @Column(name="asset_class",nullable=false,length=80) private String assetClass;
    @Column(name="acquisition_date",nullable=false) private LocalDate acquisitionDate;
    @Column(name="acquisition_cost",nullable=false,precision=19,scale=2) private BigDecimal acquisitionCost;
    @Column(name="useful_life_months",nullable=false) private Integer usefulLifeMonths;
    @Column(name="accumulated_depreciation",nullable=false,precision=19,scale=2) @Builder.Default private BigDecimal accumulatedDepreciation=BigDecimal.ZERO;
    @Column(name="residual_value",nullable=false,precision=19,scale=2) @Builder.Default private BigDecimal residualValue=BigDecimal.ZERO;
    @Column(name="cost_center_code",length=30) private String costCenterCode;
    @Column(nullable=false,length=20) @Builder.Default private String status="ACTIVE";
}
