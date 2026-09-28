package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
@Entity @Table(name="qm_inspection_plan_chars")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class InspectionPlanCharacteristic extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="plan_id") private InspectionPlan plan;
 @ManyToOne(optional=false) @JoinColumn(name="characteristic_id") private MasterInspectionCharacteristic characteristic;
 @Column(name="sample_size",nullable=false) private Integer sampleSize;
 @Column(name="sequence_no",nullable=false) private Integer sequenceNo;
}