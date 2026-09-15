package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales_inquiries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SalesInquiry extends BaseEntity {
    @Column(name="inquiry_number", nullable=false, unique=true, length=50)
    private String inquiryNumber;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customer;

    @Column(name="sales_area_id")
    private UUID salesAreaId;

    @Column(name="inquiry_date", nullable=false)
    private LocalDateTime inquiryDate;

    private LocalDate requestedValidUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    @Builder.Default private SalesInquiryStatus status = SalesInquiryStatus.DRAFT;

    @Builder.Default private String currency = "USD";
    private String notes;

    @OneToMany(mappedBy="inquiry", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default private List<SalesInquiryLine> lines = new ArrayList<>();
}
