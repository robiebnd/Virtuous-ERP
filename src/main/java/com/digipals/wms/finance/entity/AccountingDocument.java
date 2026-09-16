package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "accounting_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class AccountingDocument extends BaseEntity {
    @Column(name = "document_number", nullable = false, unique = true, length = 60)
    private String documentNumber;
    @Column(name = "document_type", nullable = false, length = 40)
    private String documentType;
    @Column(name = "document_date", nullable = false)
    private LocalDateTime documentDate;
    @Column(name = "posting_date", nullable = false)
    private LocalDateTime postingDate;
    @Column(name = "company_code", nullable = false, length = 20)
    private String companyCode;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(name = "reference_type", length = 40)
    private String referenceType;
    @Column(name = "reference_id")
    private UUID referenceId;
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;
    @Column(length = 500)
    private String description;
    @Column(name = "total_debit", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalDebit;
    @Column(name = "total_credit", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCredit;
    @Column(nullable = false, length = 20)
    private String status;
    @OneToMany(mappedBy = "accountingDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AccountingLine> lines = new ArrayList<>();

    public void addLine(AccountingLine line) {
        lines.add(line);
        line.setAccountingDocument(this);
    }
}
