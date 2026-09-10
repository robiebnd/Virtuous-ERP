package com.digipals.wms.purchaseorders.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseOrder extends BaseEntity {

    @Column(name = "po_number", unique = true, nullable = false)
    private String poNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseOrderStatus status;

    @Enumerated(EnumType.STRING)
    private ProcurementSource source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_requisition_id")
    private PurchaseRequisition purchaseRequisition;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "document_type", length = 20)
    private String documentType;

    @Column(name = "purchasing_organization", length = 40)
    private String purchasingOrganization;

    @Column(name = "purchasing_group", length = 40)
    private String purchasingGroup;

    @Column(name = "company_code", length = 40)
    private String companyCode;

    @Column(name = "payment_terms", length = 40)
    private String paymentTerms;

    @Column(name = "incoterms", length = 40)
    private String incoterms;

    @Column(name = "confirmation_control", length = 40)
    private String confirmationControl;

    @Column(name = "output_status", length = 30)
    private String outputStatus;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by")
    private User cancelledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by")
    private User closedBy;

    @OneToMany(mappedBy = "purchaseOrder", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PurchaseOrderLine> lines = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (status == null) status = PurchaseOrderStatus.DRAFT;
        if (orderDate == null) orderDate = LocalDateTime.now();
        if (source == null) source = ProcurementSource.DIRECT;
        if (documentType == null || documentType.isBlank()) documentType = "NB";
        if (outputStatus == null || outputStatus.isBlank()) outputStatus = "NOT_SENT";
        if (currency != null) currency = currency.trim().toUpperCase();
    }
}
