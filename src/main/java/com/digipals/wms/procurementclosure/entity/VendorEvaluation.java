package com.digipals.wms.procurementclosure.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.purchaseorders.entity.PurchaseOrder;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="vendor_evaluations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class VendorEvaluation extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_id",nullable=false) private Supplier supplier;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="purchase_order_id") private PurchaseOrder purchaseOrder;
    @Column(name="price_score",nullable=false,precision=5,scale=2) private BigDecimal priceScore;
    @Column(name="quality_score",nullable=false,precision=5,scale=2) private BigDecimal qualityScore;
    @Column(name="delivery_score",nullable=false,precision=5,scale=2) private BigDecimal deliveryScore;
    @Column(name="service_score",nullable=false,precision=5,scale=2) private BigDecimal serviceScore;
    @Column(name="overall_score",nullable=false,precision=5,scale=2) private BigDecimal overallScore;
    @Column(name="evaluation_date",nullable=false) private LocalDateTime evaluationDate;
    @Column(length=1000) private String remarks;
}
