package com.digipals.wms.manufacturing.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.UUID;

@Entity @Table(name="mfg_kanban_signals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class KanbanSignal extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="control_cycle_id") private KanbanControlCycle controlCycle;
 @Column(nullable=false) private String status;
 private String triggerSource;
 @Column(name="generated_production_order_id") private UUID generatedProductionOrderId;
}