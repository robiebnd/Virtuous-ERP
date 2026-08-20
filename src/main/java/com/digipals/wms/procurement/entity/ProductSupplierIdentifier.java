package com.digipals.wms.procurement.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "product_supplier_identifiers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_product_supplier_identifier_supplier_code",
                columnNames = {"supplier_id", "supplier_item_code"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductSupplierIdentifier extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "supplier_item_code", nullable = false, length = 100)
    private String supplierItemCode;

    @Column(name = "supplier_item_name", length = 255)
    private String supplierItemName;
}
