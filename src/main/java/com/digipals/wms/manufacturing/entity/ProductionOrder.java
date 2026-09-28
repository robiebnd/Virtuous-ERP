package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
@Entity @Table(name="mfg_production_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ProductionOrder extends BaseEntity {
 @Column(name="order_number",nullable=false,unique=true) private String orderNumber;
 @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
 @ManyToOne @JoinColumn(name="bom_id") private Bom bom;
 @ManyToOne @JoinColumn(name="routing_id") private Routing routing;
 @Column(name="plant_code",nullable=false) private String plantCode;
 @Column(nullable=false) private BigDecimal plannedQuantity;
 @Column(nullable=false) private BigDecimal confirmedQuantity;
 @Column(nullable=false) private String orderType;
 @Column(nullable=false) private String status;
 private LocalDate plannedStartDate;
 private LocalDate plannedFinishDate;
 @OneToMany(mappedBy="productionOrder",cascade=CascadeType.ALL,orphanRemoval=true) @Builder.Default private List<ProductionOrderMaterial> materials=new ArrayList<>();
}