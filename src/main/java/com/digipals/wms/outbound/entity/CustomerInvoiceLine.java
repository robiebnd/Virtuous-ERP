package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Entity @Table(name="customer_invoice_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class CustomerInvoiceLine extends BaseEntity {
    @JsonIgnore @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="invoice_id", nullable=false) private CustomerInvoice invoice;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_order_line_id", nullable=false) private SalesOrderLine salesOrderLine;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false) private Product product;
    @Column(nullable=false) private BigDecimal quantity;
    @Column(nullable=false) private BigDecimal unitPrice;
    @Column(nullable=false) private BigDecimal lineTotal;
}
