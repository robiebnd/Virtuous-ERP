package com.digipals.wms.finance.entity;
import com.digipals.wms.common.entity.BaseEntity; import jakarta.persistence.*; import lombok.*; import lombok.experimental.SuperBuilder; import java.math.BigDecimal;
@Entity @Table(name="consolidation_journal_lines",uniqueConstraints=@UniqueConstraint(name="uq_consolidation_journal_line",columnNames={"journal_id","line_number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ConsolidationJournalLine extends BaseEntity {
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="journal_id",nullable=false) private ConsolidationJournal journal;
 @Column(name="line_number",nullable=false) private Integer lineNumber;
 @Column(name="account_code",nullable=false,length=30) private String accountCode;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal debit;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal credit;
 @Column(name="company_code",length=20) private String companyCode;
 @Column(name="partner_company_code",length=20) private String partnerCompanyCode;
 @Column(length=255) private String description;
}