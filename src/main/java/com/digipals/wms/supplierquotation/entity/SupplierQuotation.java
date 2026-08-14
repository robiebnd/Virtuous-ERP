package com.digipals.wms.supplierquotation.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.purchaserequisition.entity.PurchaseRequisition;
import com.digipals.wms.supplier.entity.Supplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "supplier_quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SupplierQuotation extends BaseEntity {

    @Column(name = "quotation_number", nullable = false, length = 100)
    private String quotationNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_requisition_id", nullable = false)
    private PurchaseRequisition purchaseRequisition;

    @Column(name = "quotation_date")
    private LocalDate quotationDate;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false, length = 255)
    private String storedFileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SupplierQuotationStatus status = SupplierQuotationStatus.UPLOADED;
}
