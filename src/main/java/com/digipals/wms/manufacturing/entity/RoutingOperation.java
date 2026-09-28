package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
@Entity @Table(name="mfg_routing_operations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class RoutingOperation extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="routing_id") private Routing routing;
 @Column(name="operation_no",nullable=false) private Integer operationNo;
 @Column(nullable=false) private String description;
 @Column(name="work_center") private String workCenter;
 @Column(name="setup_minutes") private BigDecimal setupMinutes;
 @Column(name="run_minutes") private BigDecimal runMinutes;
}