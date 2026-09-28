package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
@Entity @Table(name="mfg_bom_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class BomItem extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="bom_id") private Bom bom;
 @ManyToOne(optional=false) @JoinColumn(name="component_product_id") private Product componentProduct;
 @Column(nullable=false) private BigDecimal quantity;
 private String uom;
 @Column(name="sequence_no",nullable=false) private Integer sequenceNo;
}