package com.digipals.wms.salesorder.entity;

import com.digipals.wms.common.entity.BaseDocument;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SalesOrder extends BaseDocument {

    @Column(name = "order_number", nullable = false, unique = true, length = 40)
    private String orderNumber;

    @Column(name = "customer_code", nullable = false, length = 40)
    private String customerCode;

    @Column(name = "sales_organization", nullable = false, length = 20)
    private String salesOrganization;

    @Column(name = "distribution_channel", nullable = false, length = 20)
    private String distributionChannel;

    @Column(name = "division", nullable = false, length = 20)
    private String division;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private SalesOrderStatus status = SalesOrderStatus.DRAFT;

    @Column(name = "sap_order_number", unique = true, length = 40)
    private String sapOrderNumber;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SalesOrderItem> items = new ArrayList<>();

    @PrePersist
    protected void prePersistSalesOrder() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (status == null) {
            status = SalesOrderStatus.DRAFT;
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
    }

    public void addItem(SalesOrderItem item) {
        items.add(item);
        item.setSalesOrder(this);
    }
}
