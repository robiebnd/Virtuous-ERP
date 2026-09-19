package com.digipals.wms.finance.entity;
import com.digipals.wms.common.entity.BaseEntity; import jakarta.persistence.*; import lombok.*; import lombok.experimental.SuperBuilder; import java.math.BigDecimal;
@Entity @Table(name="funds") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class Fund extends BaseEntity { @Column(name="fund_code",nullable=false,unique=true) String fundCode; String fundName; String currency; BigDecimal budgetAmount; boolean active; }