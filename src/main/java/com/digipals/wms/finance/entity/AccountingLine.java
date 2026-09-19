package com.digipals.wms.finance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounting_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountingLine {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accounting_document_id", nullable = false)
    private AccountingDocument accountingDocument;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gl_account_id", nullable = false)
    private GlAccount glAccount;
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal debit = BigDecimal.ZERO;
    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal credit = BigDecimal.ZERO;
    @Column(name = "company_code", nullable = false, length = 20)
    @Builder.Default
    private String companyCode = "ZW01";
    @Column(name = "cost_center", length = 40) private String costCenter;
    @Column(name = "profit_center", length = 40) private String profitCenter;
    @Column(name = "functional_area", length = 40) private String functionalArea;
    @Column(length = 40) private String segment;
    @Column(name = "line_text", length = 500) private String lineText;
    @Column(name = "internal_order_code", length = 40) private String internalOrderCode;
    @Column(name = "wbs_element", length = 60) private String wbsElement;
}
