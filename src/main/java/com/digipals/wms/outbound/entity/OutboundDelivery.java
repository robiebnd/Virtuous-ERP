package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="outbound_deliveries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class OutboundDelivery extends BaseEntity {
    @Column(name="delivery_number", nullable=false, unique=true, length=50) private String deliveryNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_order_id", nullable=false) private SalesOrder salesOrder;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="warehouse_id", nullable=false) private Warehouse warehouse;
    @Column(nullable=false) private LocalDateTime deliveryDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private DeliveryStatus status;
    @Builder.Default private Boolean picked=false;
    @Builder.Default private Boolean packed=false;
    @Builder.Default private Boolean goodsIssuePosted=false;
    private LocalDateTime goodsIssueDate;
    @OneToMany(mappedBy="delivery", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default private List<OutboundDeliveryLine> lines=new ArrayList<>();
}
