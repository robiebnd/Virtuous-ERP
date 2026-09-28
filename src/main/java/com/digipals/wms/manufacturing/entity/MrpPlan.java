package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity; import com.digipals.wms.products.Product; import jakarta.persistence.*; import lombok.*; import lombok.experimental.SuperBuilder; import java.math.*; import java.time.LocalDateTime;
@Entity @Table(name="mfg_mrp_plans") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class MrpPlan extends BaseEntity {
 @Column(name="plant_code",nullable=false) private String plantCode; @Column(name="strategy_group") private String strategyGroup;
 @Column(name="run_time",nullable=false) private LocalDateTime runTime; @Column(nullable=false) private String status;
 @Column(name="demand_count",nullable=false) private Integer demandCount; @Column(name="planned_order_count",nullable=false) private Integer plannedOrderCount;
 @ManyToOne @JoinColumn(name="product_id") private Product product;
 @Column(name="gross_requirement",precision=18,scale=2) private BigDecimal grossRequirement;
 @Column(name="available_stock",precision=18,scale=2) private BigDecimal availableStock;
 @Column(name="safety_stock",precision=18,scale=2) private BigDecimal safetyStock;
 @Column(name="net_requirement",precision=18,scale=2) private BigDecimal netRequirement;
 @Column(name="planned_order_quantity",precision=18,scale=2) private BigDecimal plannedOrderQuantity;
 @Column(name="generated_production_order_id") private java.util.UUID generatedProductionOrderId;
}