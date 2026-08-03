package com.digipals.wms.inventorybin.entity;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(
        name = "inventory_bins",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_bin",
                        columnNames = {
                                "warehouse_id",
                                "bin_id",
                                "product_id"
                        })
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InventoryBin extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder.Default
    @Column(
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal quantityOnHand =
            BigDecimal.ZERO;

    @Builder.Default
    @Column(
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal quantityReserved =
            BigDecimal.ZERO;
}
