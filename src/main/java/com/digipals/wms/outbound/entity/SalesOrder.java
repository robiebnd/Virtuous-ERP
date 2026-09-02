package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="sales_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SalesOrder extends BaseEntity {
    @Column(name="order_number", nullable=false, unique=true, length=50) private String orderNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id", nullable=false) private Customer customer;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="warehouse_id", nullable=false) private Warehouse warehouse;
    @Column(name="order_date", nullable=false) private LocalDateTime orderDate;
    private LocalDateTime requestedDeliveryDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private SalesOrderStatus status;
    @Builder.Default private String currency="USD";
    private String paymentTerms;
    @Builder.Default private BigDecimal subtotal=BigDecimal.ZERO;
    @Builder.Default private BigDecimal discountAmount=BigDecimal.ZERO;
    @Builder.Default private BigDecimal taxAmount=BigDecimal.ZERO;
    @Builder.Default private BigDecimal totalAmount=BigDecimal.ZERO;
    @Builder.Default private Boolean creditBlocked=false;
    @OneToMany(mappedBy="salesOrder", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default private List<SalesOrderLine> lines=new ArrayList<>();
}
