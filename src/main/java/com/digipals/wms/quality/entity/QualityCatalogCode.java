package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
@Entity @Table(name="qm_catalog_codes",uniqueConstraints=@UniqueConstraint(columnNames={"catalog_type","code"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class QualityCatalogCode extends BaseEntity {
 @Column(name="catalog_type",nullable=false) private String catalogType;
 @Column(nullable=false) private String code;
 @Column(nullable=false) private String description;
}