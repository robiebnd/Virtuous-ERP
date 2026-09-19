package com.digipals.wms.finance.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="cost_centers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class CostCenter extends BaseEntity {
    @Column(nullable=false,unique=true,length=30) private String code;
    @Column(nullable=false,length=150) private String name;
    @Column(name="responsible_person",length=150) private String responsiblePerson;
}
