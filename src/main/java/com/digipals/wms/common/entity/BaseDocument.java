package com.digipals.wms.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseDocument
        extends BaseEntity {

    @Column(length = 3000)
    private String remarks;

    protected BaseDocument() {
        super(null);
    }

}
