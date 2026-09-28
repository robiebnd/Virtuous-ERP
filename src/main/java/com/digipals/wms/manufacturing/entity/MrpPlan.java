package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
@Entity @Table(name="mfg_mrp_plans")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class MrpPlan extends BaseEntity {
 @Column(name="plant_code",nullable=false) private String plantCode;
 @Column(name="strategy_group") private String strategyGroup;
 @Column(name="run_time",nullable=false) private LocalDateTime runTime;
 @Column(name="status",nullable=false) private String status;
 @Column(name="demand_count",nullable=false) private Integer demandCount;
 @Column(name="planned_order_count",nullable=false) private Integer plannedOrderCount;
}