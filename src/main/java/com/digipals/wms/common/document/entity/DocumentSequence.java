package com.digipals.wms.common.document.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "document_sequences",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "document_type",
                                "financial_year"
                        })
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DocumentSequence extends BaseEntity {

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Column(nullable = false, length = 20)
    private String prefix;

    @Column(length = 20)
    private String suffix;

    @Column(name = "current_number", nullable = false)
    private Long currentNumber;

    @Column(nullable = false)
    private Integer padding;

    @Column(name = "financial_year", nullable = false)
    private Integer financialYear;

    @PrePersist
    public void prePersist() {

        if (currentNumber == null) {
            currentNumber = 1L;
        }

        if (padding == null) {
            padding = 6;
        }
    }
}