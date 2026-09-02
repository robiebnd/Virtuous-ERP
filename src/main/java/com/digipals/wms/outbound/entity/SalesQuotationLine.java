package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="sales_quotation_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SalesQuotationLine extends BaseEntity {
    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="quotation_id", nullable=false)
    private SalesQuotation quotation;

    @Column(name="line_number", nullable=false) private Integer lineNumber;
    @Column(nullable=false, length=100) private String sku;
    private String description;
    @Column(nullable=false, precision=19, scale=4) private BigDecimal quantity;
    @Column(name="unit_price", nullable=false, precision=19, scale=4) private BigDecimal unitPrice;
    @Builder.Default @Column(name="discount_amount", nullable=false, precision=19, scale=4) private BigDecimal discountAmount = BigDecimal.ZERO;
    @Builder.Default @Column(name="tax_amount", nullable=false, precision=19, scale=4) private BigDecimal taxAmount = BigDecimal.ZERO;
    @Builder.Default @Column(name="line_total", nullable=false, precision=19, scale=4) private BigDecimal lineTotal = BigDecimal.ZERO;
    private LocalDateTime requestedDeliveryDate;
}
