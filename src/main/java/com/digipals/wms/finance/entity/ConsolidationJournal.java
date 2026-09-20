package com.digipals.wms.finance.entity;
import com.digipals.wms.common.entity.BaseEntity; import jakarta.persistence.*; import lombok.*; import lombok.experimental.SuperBuilder; import java.math.BigDecimal; import java.time.*; import java.util.*;
@Entity @Table(name="consolidation_journals") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ConsolidationJournal extends BaseEntity {
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="run_id",nullable=false) private GroupReportingRun run;
 @Column(name="journal_number",nullable=false,unique=true,length=40) private String journalNumber;
 @Column(name="journal_type",nullable=false,length=30) private String journalType;
 @Column(nullable=false,length=255) private String description;
 @Column(name="posting_date",nullable=false) private LocalDate postingDate;
 @Column(nullable=false,length=20) private String status;
 @Column(name="total_debit",nullable=false,precision=19,scale=2) private BigDecimal totalDebit;
 @Column(name="total_credit",nullable=false,precision=19,scale=2) private BigDecimal totalCredit;
 @Column(name="posted_by",nullable=false,length=100) private String postedBy;
 @Column(name="posted_at",nullable=false) private LocalDateTime postedAt;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="reversal_of") private ConsolidationJournal reversalOf;
 @OneToMany(mappedBy="journal",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("lineNumber asc") private List<ConsolidationJournalLine> lines=new ArrayList<>();
}