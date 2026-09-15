package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class Customer extends BaseEntity {
    @Column(name="customer_number", nullable=false, unique=true, length=50)
    private String customerNumber;
    @Column(nullable=false) private String name;
    private String email;
    private String phone;
    private String billingAddress;
    private String shippingAddress;
    private String paymentTerms;
    private BigDecimal creditLimit;
    @Builder.Default private Boolean creditBlocked = false;
}
