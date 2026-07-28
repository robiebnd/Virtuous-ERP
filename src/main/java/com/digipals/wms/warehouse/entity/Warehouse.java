package com.digipals.wms.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import com.digipals.wms.common.entity.BaseEntity;
import lombok.experimental.SuperBuilder;
  
@Entity
@Table(name="warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Warehouse extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, updatable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String address;

    private String city;

    private String country;
}