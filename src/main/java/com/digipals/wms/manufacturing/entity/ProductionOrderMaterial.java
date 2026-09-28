package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
@Entity @Table(name="mfg_production_order_materials")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ProductionOrderMaterial extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="production_order_id") private ProductionOrder productionOrder;
 @ManyToOne(optional=false) @JoinColumn(name="component_product_id") private Product componentProduct;
 @Column(nullable=false) private BigDecimal requiredQuantity;
 @Column(nullable=false) private BigDecimal issuedQuantity;
}