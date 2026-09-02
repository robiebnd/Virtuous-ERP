package com.digipals.wms.outbound.entity;

import com.digipals.wms.bin.entity.Bin;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Entity @Table(name="outbound_delivery_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class OutboundDeliveryLine extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="delivery_id", nullable=false) private OutboundDelivery delivery;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_order_line_id", nullable=false) private SalesOrderLine salesOrderLine;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false) private Product product;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="bin_id") private Bin bin;
    @Column(nullable=false) private BigDecimal quantity;
    @Builder.Default private BigDecimal pickedQuantity=BigDecimal.ZERO;
    @Builder.Default private BigDecimal packedQuantity=BigDecimal.ZERO;
    @Builder.Default private BigDecimal issuedQuantity=BigDecimal.ZERO;
}
