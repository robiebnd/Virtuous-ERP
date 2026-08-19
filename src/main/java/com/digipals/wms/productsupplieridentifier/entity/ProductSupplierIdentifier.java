package com.digipals.wms.productsupplieridentifier.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "product_supplier_identifiers",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_product_supplier_identifier",
                columnNames = {"supplier_id", "supplier_item_code"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductSupplierIdentifier extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "supplier_item_code", nullable = false, length = 100)
    private String supplierItemCode;

    @Column(name = "supplier_item_name", length = 255)
    private String supplierItemName;
}
