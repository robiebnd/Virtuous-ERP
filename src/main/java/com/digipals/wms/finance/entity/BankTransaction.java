package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="bank_transactions", uniqueConstraints=@UniqueConstraint(name="uq_bank_transaction",columnNames={"bank_account_id","transaction_number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class BankTransaction extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="bank_account_id",nullable=false) private BankAccount bankAccount;
    @Column(name="transaction_number",nullable=false,length=80) private String transactionNumber;
    @Column(name="transaction_date",nullable=false) private LocalDate transactionDate;
    @Column(name="value_date") private LocalDate valueDate;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
    @Column(nullable=false,length=10) private String direction;
    @Column(length=150) private String reference;
    @Column(length=500) private String description;
    @Column(nullable=false,length=20) private String status;
    @Column(name="accounting_document_id") private UUID accountingDocumentId;
}