package com.digipals.wms.bintransfer.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "bin_transfer_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BinTransferLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_transfer_id", nullable = false)
    private BinTransfer binTransfer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(length = 500)
    private String remarks;
}
