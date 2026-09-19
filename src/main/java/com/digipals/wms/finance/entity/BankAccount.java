package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="bank_accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class BankAccount extends BaseEntity {
    @Column(name="account_number",nullable=false,unique=true,length=60) private String accountNumber;
    @Column(name="bank_name",nullable=false,length=150) private String bankName;
    @Column(name="branch_name",length=150) private String branchName;
    @Column(nullable=false,length=3) private String currency;
    @Column(name="gl_account_code",nullable=false,length=20) private String glAccountCode;
}
