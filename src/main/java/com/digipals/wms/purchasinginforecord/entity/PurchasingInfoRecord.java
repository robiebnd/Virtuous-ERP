package com.digipals.wms.purchasinginforecord.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.productsupplieridentifier.entity.ProductSupplierIdentifier;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "purchasing_info_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_pir_supplier_product_warehouse",
                columnNames = {"product_supplier_identifier_id", "warehouse_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchasingInfoRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_supplier_identifier_id", nullable = false)
    private ProductSupplierIdentifier supplierProduct;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "last_purchase_price", precision = 19, scale = 4)
    private BigDecimal lastPurchasePrice;

    @Column(name = "standard_order_quantity", precision = 19, scale = 4)
    private BigDecimal standardOrderQuantity;

    @Column(name = "planned_delivery_days")
    private Integer plannedDeliveryDays;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "regular_supplier", nullable = false)
    private Boolean regularSupplier = false;

    @Column(name = "automatic_sourcing", nullable = false)
    private Boolean automaticSourcing = false;
}
