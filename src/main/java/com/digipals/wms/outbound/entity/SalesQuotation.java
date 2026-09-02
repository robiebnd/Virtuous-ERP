package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="sales_quotations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SalesQuotation extends BaseEntity {
    @Column(name="quotation_number", nullable=false, unique=true, length=50)
    private String quotationNumber;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="inquiry_id")
    private SalesInquiry inquiry;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customer;

    @Column(name="sales_area_id")
    private UUID salesAreaId;

    @Column(name="quotation_date", nullable=false)
    private LocalDateTime quotationDate;

    @Column(name="valid_from", nullable=false)
    private LocalDate validFrom;

    @Column(name="valid_to", nullable=false)
    private LocalDate validTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    @Builder.Default private SalesQuotationStatus status = SalesQuotationStatus.DRAFT;

    @Builder.Default private String currency = "USD";
    @Builder.Default private BigDecimal subtotal = BigDecimal.ZERO;
    @Builder.Default private BigDecimal discountAmount = BigDecimal.ZERO;
    @Builder.Default private BigDecimal taxAmount = BigDecimal.ZERO;
    @Builder.Default private BigDecimal totalAmount = BigDecimal.ZERO;
    private String notes;
    private String convertedOrderNumber;

    @OneToMany(mappedBy="quotation", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default private List<SalesQuotationLine> lines = new ArrayList<>();
}
