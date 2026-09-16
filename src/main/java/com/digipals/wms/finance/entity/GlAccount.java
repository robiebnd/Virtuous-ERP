package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "gl_accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class GlAccount extends BaseEntity {
    @Column(name = "account_code", nullable = false, unique = true, length = 20)
    private String accountCode;
    @Column(name = "account_name", nullable = false, length = 150)
    private String accountName;
    @Column(name = "account_type", nullable = false, length = 30)
    private String accountType;
    @Column(name = "control_account", nullable = false)
    @Builder.Default
    private Boolean controlAccount = false;
}
