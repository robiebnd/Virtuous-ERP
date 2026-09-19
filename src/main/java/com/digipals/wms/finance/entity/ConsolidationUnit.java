package com.digipals.wms.finance.entity;
import com.digipals.wms.common.entity.BaseEntity; import jakarta.persistence.*; import lombok.*; import lombok.experimental.SuperBuilder;
@Entity @Table(name="consolidation_units") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ConsolidationUnit extends BaseEntity { @Column(name="unit_code",nullable=false,unique=true) String unitCode; String unitName; String localCurrency; java.math.BigDecimal ownershipPercent; boolean active; }