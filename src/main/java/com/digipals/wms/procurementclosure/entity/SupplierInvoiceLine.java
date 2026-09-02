package com.digipals.wms.procurementclosure.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Entity @Table(name="supplier_invoice_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SupplierInvoiceLine extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="supplier_invoice_id",nullable=false) private SupplierInvoice invoice;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="purchase_order_line_id",nullable=false) private PurchaseOrderLine purchaseOrderLine;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) private Product product;
    @Column(nullable=false,precision=18,scale=2) private BigDecimal quantity;
    @Column(name="unit_price",nullable=false,precision=18,scale=2) private BigDecimal unitPrice;
    @Column(name="line_total",nullable=false,precision=18,scale=2) private BigDecimal lineTotal;
}
