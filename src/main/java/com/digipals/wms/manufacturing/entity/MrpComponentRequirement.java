package com.digipals.wms.manufacturing.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="mfg_mrp_component_requirements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class MrpComponentRequirement extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="mrp_plan_id") private MrpPlan mrpPlan;
 @ManyToOne(optional=false) @JoinColumn(name="component_product_id") private Product componentProduct;
 @Column(name="parent_product_id",nullable=false) private UUID parentProductId;
 @Column(name="gross_requirement",nullable=false,precision=18,scale=2) private BigDecimal grossRequirement;
 @Column(name="available_stock",nullable=false,precision=18,scale=2) private BigDecimal availableStock;
 @Column(name="open_supply",nullable=false,precision=18,scale=2) private BigDecimal openSupply;
 @Column(name="net_requirement",nullable=false,precision=18,scale=2) private BigDecimal netRequirement;
 @Column(name="planned_order_quantity",nullable=false,precision=18,scale=2) private BigDecimal plannedOrderQuantity;
 @Column(name="generated_production_order_id") private UUID generatedProductionOrderId;
 @Column(name="bom_level",nullable=false) private Integer bomLevel;
}