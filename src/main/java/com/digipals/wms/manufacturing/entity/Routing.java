package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.*;
@Entity @Table(name="mfg_routings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class Routing extends BaseEntity {
 @Column(name="routing_number",nullable=false,unique=true) private String routingNumber;
 @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
 @Column(nullable=false) private String plantCode;
 private String routingVersion;
 @Column(nullable=false) private String status;
 @OneToMany(mappedBy="routing",cascade=CascadeType.ALL,orphanRemoval=true) @Builder.Default private List<RoutingOperation> operations=new ArrayList<>();
}