package com.digipals.wms.purchaserequisition.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.supplier.entity.Supplier;
import com.digipals.wms.users.entity.User;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_requisitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseRequisition extends BaseEntity {

    @Column(name = "requisition_number", nullable = false, unique = true, length = 50)
    private String requisitionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejected_by")
    private User rejectedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by")
    private User cancelledBy;

    @Column(name = "department", nullable = false, length = 150)
    private String department;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "document_type", length = 20)
    private String documentType;

    @Column(name = "purchasing_group", length = 40)
    private String purchasingGroup;

    @Column(name = "plant_code", length = 40)
    private String plantCode;

    @Column(name = "storage_location", length = 40)
    private String storageLocation;

    @Column(name = "item_category", length = 30)
    private String itemCategory;

    @Column(name = "account_assignment_category", length = 5)
    private String accountAssignmentCategory;

    @Column(name = "requested_delivery_date")
    private LocalDateTime requestedDeliveryDate;

    @Column(name = "valuation_price", precision = 18, scale = 2)
    private java.math.BigDecimal valuationPrice;

    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PurchaseRequisitionStatus status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @PrePersist
    protected void prePersist() {
        if (status == null) status = PurchaseRequisitionStatus.DRAFT;
        if (documentType == null || documentType.isBlank()) documentType = "NB";
        if (itemCategory == null || itemCategory.isBlank()) itemCategory = "STANDARD";
        if (accountAssignmentCategory == null) accountAssignmentCategory = "";
        if (approvalLevel == null) approvalLevel = 0;
        if (currency != null) currency = currency.trim().toUpperCase();
    }
}
