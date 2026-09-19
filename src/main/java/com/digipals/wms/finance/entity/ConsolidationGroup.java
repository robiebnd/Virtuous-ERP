package com.digipals.wms.finance.entity;
import com.digipals.wms.common.entity.BaseEntity; import jakarta.persistence.*; import lombok.*; import lombok.experimental.SuperBuilder;
@Entity @Table(name="consolidation_groups") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ConsolidationGroup extends BaseEntity { @Column(name="group_code",nullable=false,unique=true) String groupCode; String groupName; String reportingCurrency; boolean active; }