package com.digipals.wms.finance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="group_reporting_balances")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupReportingBalance {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="run_id",nullable=false) private GroupReportingRun run;
    @Column(name="company_code",nullable=false,length=20) private String companyCode;
    @Column(name="account_code",nullable=false,length=20) private String accountCode;
    @Column(name="account_name",nullable=false,length=150) private String accountName;
    @Column(name="account_type",nullable=false,length=30) private String accountType;
    @Column(name="local_debit",nullable=false,precision=19,scale=2) private BigDecimal localDebit;
    @Column(name="local_credit",nullable=false,precision=19,scale=2) private BigDecimal localCredit;
    @Column(name="fx_rate",nullable=false,precision=19,scale=8) private BigDecimal fxRate;
    @Column(name="translated_debit",nullable=false,precision=19,scale=2) private BigDecimal translatedDebit;
    @Column(name="translated_credit",nullable=false,precision=19,scale=2) private BigDecimal translatedCredit;
    @Column(name="elimination_debit",nullable=false,precision=19,scale=2) private BigDecimal eliminationDebit;
    @Column(name="elimination_credit",nullable=false,precision=19,scale=2) private BigDecimal eliminationCredit;
    @Column(name="final_debit",nullable=false,precision=19,scale=2) private BigDecimal finalDebit;
    @Column(name="final_credit",nullable=false,precision=19,scale=2) private BigDecimal finalCredit;
}
