package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="group_account_mappings", uniqueConstraints=@UniqueConstraint(name="uq_group_account_mapping", columnNames={"group_id","company_code","local_account_code"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class GroupAccountMapping extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="group_id", nullable=false)
    private ConsolidationGroup group;
    @Column(name="company_code",nullable=false,length=20) private String companyCode;
    @Column(name="local_account_code",nullable=false,length=20) private String localAccountCode;
    @Column(name="group_account_code",nullable=false,length=20) private String groupAccountCode;
    @Column(name="group_account_name",nullable=false,length=150) private String groupAccountName;
    @Column(name="group_account_type",nullable=false,length=30) private String groupAccountType;
}
