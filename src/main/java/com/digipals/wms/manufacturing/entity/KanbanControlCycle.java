package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
@Entity @Table(name="mfg_kanban_control_cycles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class KanbanControlCycle extends BaseEntity {
 @Column(name="cycle_code",nullable=false,unique=true) private String cycleCode;
 @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
 @Column(name="plant_code",nullable=false) private String plantCode;
 @Column(nullable=false) private Integer containers;
 @Column(name="container_quantity",nullable=false) private BigDecimal containerQuantity;
 @Column(name="replenishment_strategy",nullable=false) private String replenishmentStrategy;
}