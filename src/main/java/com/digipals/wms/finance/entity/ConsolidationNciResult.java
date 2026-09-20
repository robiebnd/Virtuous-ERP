package com.digipals.wms.finance.entity;
import com.digipals.wms.common.entity.BaseEntity; import jakarta.persistence.*; import lombok.*; import lombok.experimental.SuperBuilder; import java.math.BigDecimal;
@Entity @Table(name="consolidation_nci_results",uniqueConstraints=@UniqueConstraint(name="uq_consolidation_nci_run_unit",columnNames={"run_id","unit_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ConsolidationNciResult extends BaseEntity {
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="run_id",nullable=false) private GroupReportingRun run;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="unit_id",nullable=false) private ConsolidationUnit unit;
 @Column(name="ownership_percent",nullable=false,precision=7,scale=4) private BigDecimal ownershipPercent;
 @Column(name="nci_percent",nullable=false,precision=7,scale=4) private BigDecimal nciPercent;
 @Column(name="net_assets",nullable=false,precision=19,scale=2) private BigDecimal netAssets;
 @Column(name="nci_amount",nullable=false,precision=19,scale=2) private BigDecimal nciAmount;
}