package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
@Entity @Table(name="qm_quality_info_records",uniqueConstraints=@UniqueConstraint(columnNames={"supplier_id","product_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class QualityInfoRecord extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="supplier_id") private Supplier supplier;
 @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
 @Column(nullable=false) private Boolean inspectionRequired;
 private Integer skipLotAfterGoodLots;
}