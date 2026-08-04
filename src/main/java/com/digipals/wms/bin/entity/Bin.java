package com.digipals.wms.bin.entity;

import com.digipals.wms.bin.entity.BinStatus;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "bins", uniqueConstraints = {
                @UniqueConstraint(name = "uk_bin_code", columnNames = {
                                "warehouse_id",
                                "code"
                }),
                @UniqueConstraint(name = "uk_bin_barcode", columnNames = "barcode")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Bin extends BaseEntity {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "warehouse_id", nullable = false)
        private Warehouse warehouse;

        @Column(nullable = false, length = 30)
        private String code;

        @Column(nullable = false, length = 100)
        private String name;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private BinType type;

        @Column(nullable = false)
        @Builder.Default
        private Boolean receivingBin = false;


        @Column(precision = 18, scale = 2)
        @Builder.Default
        private BigDecimal capacity = BigDecimal.ZERO;

        @Builder.Default
        private Boolean active = true;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @Builder.Default
        private BinStatus status = BinStatus.AVAILABLE;

        @Column(unique = true)
        private String barcode;

        private Integer sequence;

        @Builder.Default
        @Column(name = "used_capacity", nullable = false, precision = 18, scale = 2)
        private BigDecimal usedCapacity = BigDecimal.ZERO;

        @Column(length = 500)
        private String description;

}
