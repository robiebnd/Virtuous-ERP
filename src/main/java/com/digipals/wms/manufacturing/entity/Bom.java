package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.*;
@Entity @Table(name="mfg_boms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class Bom extends BaseEntity {
 @Column(name="bom_number",nullable=false,unique=true) private String bomNumber;
 @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
 @Column(nullable=false) private String plantCode;
 private String bomVersion;
 @Column(nullable=false) private String status;
 @OneToMany(mappedBy="bom",cascade=CascadeType.ALL,orphanRemoval=true) @Builder.Default private List<BomItem> items=new ArrayList<>();
}