package com.digipals.wms.purchaserequisition.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import com.digipals.wms.purchasinginforecord.entity.PurchasingInfoRecord;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_requisition_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PurchaseRequisitionLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_requisition_id", nullable = false)
    private PurchaseRequisition purchaseRequisition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(name = "estimated_unit_cost", precision = 18, scale = 2)
    private BigDecimal estimatedUnitCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_supplier_id")
    private Supplier sourceSupplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchasing_info_record_id")
    private PurchasingInfoRecord purchasingInfoRecord;

    @Column(name = "item_category", length = 30)
    private String itemCategory;

    @Column(name = "account_assignment_category", length = 5)
    private String accountAssignmentCategory;

    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure;

    @Column(name = "requested_delivery_date")
    private LocalDateTime requestedDeliveryDate;

    @Column(name = "valuation_price", precision = 18, scale = 2)
    private BigDecimal valuationPrice;

    @Column(length = 500)
    private String remarks;

    @PrePersist
    protected void prePersist() {
        if (itemCategory == null || itemCategory.isBlank()) itemCategory = "STANDARD";
        if (accountAssignmentCategory == null) accountAssignmentCategory = "";
        if (unitOfMeasure == null || unitOfMeasure.isBlank()) unitOfMeasure = "EA";
        if (valuationPrice == null) valuationPrice = estimatedUnitCost;
    }
}
