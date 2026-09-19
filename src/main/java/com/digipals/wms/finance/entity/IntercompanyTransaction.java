package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="intercompany_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class IntercompanyTransaction extends BaseEntity {
    @Column(name="transaction_number",nullable=false,unique=true,length=80) private String transactionNumber;
    @Column(name="transaction_date",nullable=false) private LocalDate transactionDate;
    @Column(name="source_company_code",nullable=false,length=20) private String sourceCompanyCode;
    @Column(name="target_company_code",nullable=false,length=20) private String targetCompanyCode;
    @Column(nullable=false,length=3) private String currency;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
    @Column(name="source_account_code",nullable=false,length=20) private String sourceAccountCode;
    @Column(name="target_account_code",nullable=false,length=20) private String targetAccountCode;
    @Column(name="source_debit_account_code",length=20) private String sourceDebitAccountCode;
    @Column(name="source_credit_account_code",length=20) private String sourceCreditAccountCode;
    @Column(name="target_debit_account_code",length=20) private String targetDebitAccountCode;
    @Column(name="target_credit_account_code",length=20) private String targetCreditAccountCode;
    @Column(name="source_document_id") private UUID sourceDocumentId;
    @Column(name="target_document_id") private UUID targetDocumentId;
    @Column(length=500) private String description;
    @Column(nullable=false,length=20) private String status;
}
