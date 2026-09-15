package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="sales_inquiry_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SalesInquiryLine extends BaseEntity {
    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="inquiry_id", nullable=false)
    private SalesInquiry inquiry;

    @Column(name="line_number", nullable=false)
    private Integer lineNumber;
    @Column(nullable=false, length=100) private String sku;
    private String description;
    @Column(nullable=false, precision=19, scale=4) private BigDecimal quantity;
    private LocalDateTime requestedDeliveryDate;
}
