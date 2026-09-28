package com.digipals.wms.quality.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
@Entity @Table(name="qm_usage_decisions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class UsageDecision extends BaseEntity {
 @OneToOne(optional=false) @JoinColumn(name="inspection_lot_id",unique=true) private InspectionLot inspectionLot;
 @Column(name="decision_code",nullable=false) private String decisionCode;
 @Column(name="stock_action",nullable=false) private String stockAction;
 private String remarks;
}