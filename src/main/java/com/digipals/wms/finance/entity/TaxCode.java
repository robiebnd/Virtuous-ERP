package com.digipals.wms.finance.entity;
import com.digipals.wms.common.entity.BaseEntity; import jakarta.persistence.*; import lombok.*; import lombok.experimental.SuperBuilder; import java.math.BigDecimal;
@Entity @Table(name="tax_codes") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class TaxCode extends BaseEntity { @Column(name="tax_code",nullable=false,unique=true) String taxCode; String description; BigDecimal rate; String inputAccountCode; String outputAccountCode; boolean withholding; boolean active; }