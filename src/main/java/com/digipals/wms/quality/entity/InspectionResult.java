package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
@Entity @Table(name="qm_inspection_results")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class InspectionResult extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="inspection_lot_id") private InspectionLot inspectionLot;
 @ManyToOne(optional=false) @JoinColumn(name="characteristic_id") private MasterInspectionCharacteristic characteristic;
 private BigDecimal measuredValue;
 private String qualitativeResult;
 @Column(nullable=false) private String resultStatus;
 private String defectType;
 private String defectCause;
}