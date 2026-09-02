package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Entity @Table(name="sales_order_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SalesOrderLine extends BaseEntity {
    @JsonIgnore @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_order_id", nullable=false) private SalesOrder salesOrder;
    @Column(name="line_number", nullable=false) private Integer lineNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false) private Product product;
    @Column(nullable=false) private BigDecimal quantity;
    @Column(nullable=false) private BigDecimal unitPrice;
    @Builder.Default private BigDecimal discountAmount=BigDecimal.ZERO;
    @Builder.Default private BigDecimal taxAmount=BigDecimal.ZERO;
    @Column(nullable=false) private BigDecimal lineTotal;
    @Builder.Default private BigDecimal quantityDelivered=BigDecimal.ZERO;
    @Builder.Default private BigDecimal quantityBilled=BigDecimal.ZERO;
}
