package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @Table(name="mfg_capacity_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class CapacityRecord extends BaseEntity {
 @Column(name="work_center",nullable=false) private String workCenter;
 @Column(name="capacity_date",nullable=false) private LocalDate capacityDate;
 @Column(name="available_minutes",nullable=false) private BigDecimal availableMinutes;
 @Column(name="planned_minutes",nullable=false) private BigDecimal plannedMinutes;
}