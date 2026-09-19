package com.digipals.wms.finance.entity;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name="profitability_postings") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfitabilityPosting {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="accounting_line_id",nullable=false,unique=true) private AccountingLine accountingLine;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="segment_id",nullable=false) private ProfitabilitySegment segment;
 @Column(name="posting_date",nullable=false) private LocalDate postingDate;
 @Column(name="revenue_amount",nullable=false,precision=19,scale=2) private BigDecimal revenueAmount;
 @Column(name="cost_amount",nullable=false,precision=19,scale=2) private BigDecimal costAmount;
 @Column(name="quantity",nullable=false,precision=19,scale=6) private BigDecimal quantity;
 @Column(nullable=false,length=3) private String currency;
}