package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.*;
@Entity @Table(name="qm_inspection_plans")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class InspectionPlan extends BaseEntity {
 @Column(name="plan_number",nullable=false,unique=true) private String planNumber;
 @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
 @Column(nullable=false) private String plantCode;
 private String planVersion;
 @Column(nullable=false) private String status;
 @OneToMany(mappedBy="plan",cascade=CascadeType.ALL,orphanRemoval=true) @Builder.Default private List<InspectionPlanCharacteristic> characteristics=new ArrayList<>();
}