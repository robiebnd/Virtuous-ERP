package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
@Entity @Table(name="qm_master_inspection_characteristics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class MasterInspectionCharacteristic extends BaseEntity {
 @Column(name="code",nullable=false,unique=true) private String code;
 @Column(name="name",nullable=false) private String name;
 @Column(name="data_type",nullable=false) private String dataType;
 private BigDecimal lowerTolerance;
 private BigDecimal upperTolerance;
 @Column(name="unit_of_measure") private String unitOfMeasure;
}