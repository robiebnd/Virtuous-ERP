package com.digipals.wms.supplierquotation.dto;

import com.digipals.wms.supplierquotation.entity.SupplierQuotationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class SupplierQuotationResponse {

    private UUID id;
    private String quotationNumber;
    private UUID supplierId;
    private String supplierCode;
    private String supplierName;
    private UUID purchaseRequisitionId;
    private String requisitionNumber;
    private LocalDate quotationDate;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private SupplierQuotationStatus status;
    private LocalDateTime uploadedAt;
}
