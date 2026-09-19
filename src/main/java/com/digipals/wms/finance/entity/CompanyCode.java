package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="company_codes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class CompanyCode extends BaseEntity {
    @Column(name="company_code", nullable=false, unique=true, length=20) private String companyCode;
    @Column(name="company_name", nullable=false, length=150) private String companyName;
    @Column(name="country_code", nullable=false, length=3) private String countryCode;
    @Column(name="functional_currency", nullable=false, length=3) private String functionalCurrency;
    @Column(name="fiscal_year_variant", nullable=false, length=20) private String fiscalYearVariant;
    @Column(name="reporting_currency", length=3) private String reportingCurrency;
}
