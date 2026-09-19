package com.digipals.wms.finance.entity;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name="tax_postings") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaxPosting {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="accounting_document_id",nullable=false) private AccountingDocument accountingDocument;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="accounting_line_id") private AccountingLine accountingLine;
 @Column(name="company_code",nullable=false,length=20) private String companyCode;
 @Column(name="tax_code",nullable=false,length=20) private String taxCode;
 @Column(name="tax_type",nullable=false,length=30) private String taxType;
 @Column(length=50) private String jurisdiction;
 @Column(name="taxable_base",nullable=false,precision=19,scale=2) private BigDecimal taxableBase;
 @Column(name="tax_amount",nullable=false,precision=19,scale=2) private BigDecimal taxAmount;
 @Column(name="input_output",nullable=false,length=10) private String inputOutput;
 @Column(name="tax_account_code",nullable=false,length=20) private String taxAccountCode;
 @Column(name="recoverable_amount",nullable=false,precision=19,scale=2) private BigDecimal recoverableAmount;
}