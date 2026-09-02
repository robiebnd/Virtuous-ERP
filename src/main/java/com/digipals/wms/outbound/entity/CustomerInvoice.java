package com.digipals.wms.outbound.entity;

import com.digipals.wms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="customer_invoices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class CustomerInvoice extends BaseEntity {
    @Column(name="invoice_number", nullable=false, unique=true, length=50) private String invoiceNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id", nullable=false) private Customer customer;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sales_order_id", nullable=false) private SalesOrder salesOrder;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="delivery_id") private OutboundDelivery delivery;
    @Column(nullable=false) private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private InvoiceStatus status;
    @Builder.Default private String currency="USD";
    @Builder.Default private BigDecimal subtotal=BigDecimal.ZERO;
    @Builder.Default private BigDecimal discountAmount=BigDecimal.ZERO;
    @Builder.Default private BigDecimal taxAmount=BigDecimal.ZERO;
    @Builder.Default private BigDecimal totalAmount=BigDecimal.ZERO;
    @Builder.Default private BigDecimal amountPaid=BigDecimal.ZERO;
    @Builder.Default private BigDecimal balanceDue=BigDecimal.ZERO;
    @OneToMany(mappedBy="invoice", cascade=CascadeType.ALL, orphanRemoval=true)
    @Builder.Default private List<CustomerInvoiceLine> lines=new ArrayList<>();
}
