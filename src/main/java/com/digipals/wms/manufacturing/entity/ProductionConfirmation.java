package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="mfg_production_confirmations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ProductionConfirmation extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="production_order_id") private ProductionOrder productionOrder;
 @Column(nullable=false) private BigDecimal quantity;
 @Column(name="scrap_quantity",nullable=false) private BigDecimal scrapQuantity;
 @Column(name="confirmation_time",nullable=false) private LocalDateTime confirmationTime;
 private String operation;
 private String remarks;
}