package com.digipals.wms.vendorevaluation.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="vendor_evaluations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class VendorEvaluation extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="supplier_id", nullable=false)
    private Supplier supplier;
    @Column(name="purchase_order_id", nullable=false)
    private UUID purchaseOrderId;
    @Column(name="purchase_order_number", nullable=false, length=50)
    private String purchaseOrderNumber;
    @Column(nullable=false, precision=5, scale=2) private BigDecimal priceScore;
    @Column(nullable=false, precision=5, scale=2) private BigDecimal qualityScore;
    @Column(nullable=false, precision=5, scale=2) private BigDecimal deliveryScore;
    @Column(nullable=false, precision=5, scale=2) private BigDecimal serviceScore;
    @Column(nullable=false, precision=5, scale=2) private BigDecimal overallScore;
    @Column(nullable=false) private LocalDateTime evaluationDate;
    @Column(length=1000) private String remarks;
    @PrePersist @PreUpdate
    private void calculateOverall() {
        if (priceScore == null) priceScore=BigDecimal.ZERO;
        if (qualityScore == null) qualityScore=BigDecimal.ZERO;
        if (deliveryScore == null) deliveryScore=BigDecimal.ZERO;
        if (serviceScore == null) serviceScore=BigDecimal.ZERO;
        overallScore=priceScore.add(qualityScore).add(deliveryScore).add(serviceScore).divide(BigDecimal.valueOf(4),2,java.math.RoundingMode.HALF_UP);
        if (evaluationDate==null) evaluationDate=LocalDateTime.now();
    }
}
