package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

@Entity
@Table(name="profitability_segments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ProfitabilitySegment extends BaseEntity {
    @Column(name="segment_code",nullable=false,unique=true,length=80) private String segmentCode;
    @Column(name="segment_name",nullable=false,length=150) private String segmentName;
    @Column(name="company_code",nullable=false,length=20) private String companyCode;
    @Column(name="customer_id") private UUID customerId;
    @Column(name="product_code",length=80) private String productCode;
    @Column(name="sales_channel",length=80) private String salesChannel;
    @Column(name="market_region",length=80) private String marketRegion;
    @Column(name="customer_group",length=80) private String customerGroup;
    @Column(name="product_group",length=80) private String productGroup;
}
